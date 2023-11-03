package com.jihai.bitfree.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jihai.bitfree.ability.MonitorAbility;
import com.jihai.bitfree.base.enums.CanReadEnum;
import com.jihai.bitfree.base.enums.MessageTypeEnum;
import com.jihai.bitfree.dao.MessageNoticeDAO;
import com.jihai.bitfree.dao.NotificationDAO;
import com.jihai.bitfree.dto.req.NotificationResp;
import com.jihai.bitfree.dto.resp.NotificationDetailResp;
import com.jihai.bitfree.entity.MessageNoticeDO;
import com.jihai.bitfree.entity.NotificationDO;
import com.jihai.bitfree.support.Observable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    private NotificationDAO notificationDAO;

    @Autowired
    private MessageNoticeDAO messageNoticeDAO;

    @Autowired
    private MonitorAbility monitorAbility;

    public List<NotificationResp> pageQuery(int start, Integer size, Long userId) {
        List<NotificationDO> notificationDOList = notificationDAO.pageQuery(start, size, MessageTypeEnum.NOTIFICATION.getType());

        // FIXME 这里在过滤情况下，分页显示条数与页面条数不一致
        notificationDOList.removeIf(e -> {
            // 自己不在被通知范围内
            List<Long> needNotificationUserList = getNeedNotificationUserList(e);
            return ! CollectionUtils.isEmpty(needNotificationUserList) && ! needNotificationUserList.contains(userId);
        });

        if (CollectionUtils.isEmpty(notificationDOList)) return Collections.emptyList();

        List<MessageNoticeDO> messageNoticeDOS = messageNoticeDAO.queryByMessageIdList(MessageTypeEnum.NOTIFICATION.getType(), notificationDOList.stream().map(e -> e.getId()).collect(Collectors.toList()), userId);

        return notificationDOList.stream().map(notificationDO -> {
            NotificationResp notificationResp = new NotificationResp();
            boolean canRead = CanReadEnum.YES.getValue().equals(notificationDO.getCanRead());
            notificationResp.setCanRead(canRead);
            if (canRead) {
                boolean unRead = messageNoticeDOS.stream().noneMatch(e -> e.getMessageId().equals(notificationDO.getId()) && userId.equals(e.getUserId()));
                notificationResp.setUnRead(unRead);
            }
            BeanUtils.copyProperties(notificationDO, notificationResp);
            return notificationResp;
        }).collect(Collectors.toList());
    }

    public Integer total() {
        return notificationDAO.total();
    }

    public NotificationDetailResp detail(Long id) {
        NotificationDO notificationDO = notificationDAO.detail(id);

        NotificationDetailResp notificationDetailResp = new NotificationDetailResp();
        BeanUtils.copyProperties(notificationDO, notificationDetailResp);
        notificationDetailResp.setCanRead(CanReadEnum.YES.getValue().equals(notificationDO.getCanRead()));

        return notificationDetailResp;
    }

    public Integer unReadNotificationCount(Long userId) {
        List<NotificationDO> notificationDOList = notificationDAO.getAll();
        if (CollectionUtils.isEmpty(notificationDOList)) return 0;
        List<MessageNoticeDO> messageNoticeDOS = messageNoticeDAO.queryByMessageIdList(MessageTypeEnum.NOTIFICATION.getType(), notificationDOList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList()), userId);

        notificationDOList.removeIf(e -> {
            // 过滤自己不在被通知范围内
            List<Long> needNotificationUserList = getNeedNotificationUserList(e);
            return ! CollectionUtils.isEmpty(needNotificationUserList) && ! needNotificationUserList.contains(userId);
        });
        if (CollectionUtils.isEmpty(messageNoticeDOS)) return notificationDOList.size();

        Set<Long> readMessageIdSet = messageNoticeDOS.stream().map(e -> e.getMessageId()).collect(Collectors.toSet());

        // 过滤已经通知过
        notificationDOList.removeIf(e -> readMessageIdSet.contains(e.getId()));

        return notificationDOList.size();
    }

    private List<Long> getNeedNotificationUserList(NotificationDO notificationDO) {
        if (StringUtils.isEmpty(notificationDO.getUserList())) return Collections.emptyList();

        try {
            String[] userListStrArr = notificationDO.getUserList().split(",");
            return Arrays.stream(userListStrArr).map(Long::valueOf).distinct().collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取用户配置配置异常", e);
            monitorAbility.sendMsg("用户通知配置错误: " + JSONObject.toJSONString(notificationDO));
            // 避免错发，直接限制成一个不存在的id
            return Lists.newArrayList(-1L);
        }
    }

    @Autowired
    private Observable observable;

    @Transactional
    public Boolean read(Long id, Long userId) {
        MessageNoticeDO messageNoticeDO = new MessageNoticeDO();
        messageNoticeDO.setMessageId(id);
        messageNoticeDO.setUserId(userId);
        messageNoticeDO.setType(MessageTypeEnum.NOTIFICATION.getType());

        messageNoticeDAO.insert(messageNoticeDO);
        return true;
    }
}
