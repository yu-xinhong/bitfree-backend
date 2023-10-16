package com.jihai.bitfree.service;

import com.jihai.bitfree.base.enums.MessageTypeEnum;
import com.jihai.bitfree.dao.MessageNoticeDAO;
import com.jihai.bitfree.dao.NotificationDAO;
import com.jihai.bitfree.dto.req.NotificationResp;
import com.jihai.bitfree.dto.resp.NotificationDetailResp;
import com.jihai.bitfree.entity.MessageNoticeDO;
import com.jihai.bitfree.entity.NotificationDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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

    public List<NotificationResp> pageQuery(int start, Integer size, Long userId) {
        List<NotificationDO> notificationDOList = notificationDAO.pageQuery(start, size, MessageTypeEnum.NOTIFICATION.getType());
        if (CollectionUtils.isEmpty(notificationDOList)) return Collections.emptyList();

        List<MessageNoticeDO> messageNoticeDOS = messageNoticeDAO.queryByMessageIdList(MessageTypeEnum.NOTIFICATION.getType(), notificationDOList.stream().map(e -> e.getId()).collect(Collectors.toList()), userId);

        return notificationDOList.stream().map(notificationDO -> {
            boolean isRead = messageNoticeDOS.stream().noneMatch(e -> e.getMessageId().equals(notificationDO.getId()) && userId.equals(e.getUserId()));

            NotificationResp notificationResp = new NotificationResp();
            BeanUtils.copyProperties(notificationDO, notificationResp);
            notificationResp.setUnRead(isRead);
            return notificationResp;
        }).collect(Collectors.toList());
    }

    public Integer total() {
        return notificationDAO.total();
    }

    public NotificationDetailResp detail(Long id, Long userId) {
        NotificationDO notificationDO = notificationDAO.detail(id);

        NotificationDetailResp notificationDetailResp = new NotificationDetailResp();
        BeanUtils.copyProperties(notificationDO, notificationDetailResp);

        return notificationDetailResp;
    }

    public Integer unReadNotificationCount(Long userId) {
        List<NotificationDO> notificationDOList = notificationDAO.getAll();
        if (CollectionUtils.isEmpty(notificationDOList)) return 0;
        List<MessageNoticeDO> messageNoticeDOS = messageNoticeDAO.queryByMessageIdList(MessageTypeEnum.NOTIFICATION.getType(), notificationDOList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList()), userId);

        if (CollectionUtils.isEmpty(messageNoticeDOS)) return notificationDOList.size();

        Set<Long> readMessageIdSet = messageNoticeDOS.stream().map(e -> e.getMessageId()).collect(Collectors.toSet());
        notificationDOList.removeIf(e -> readMessageIdSet.contains(e.getId()));
        return notificationDOList.size();
    }

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
