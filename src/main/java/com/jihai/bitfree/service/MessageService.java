package com.jihai.bitfree.service;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.base.enums.MessageTypeEnum;
import com.jihai.bitfree.base.enums.OperateTypeEnum;
import com.jihai.bitfree.bo.UserRemarkBO;
import com.jihai.bitfree.constants.LockKeyConstants;
import com.jihai.bitfree.dao.MessageDAO;
import com.jihai.bitfree.dao.MessageNoticeDAO;
import com.jihai.bitfree.dao.OperateLogDAO;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.dto.resp.MessageResp;
import com.jihai.bitfree.dto.resp.UserResp;
import com.jihai.bitfree.entity.MessageDO;
import com.jihai.bitfree.entity.MessageNoticeDO;
import com.jihai.bitfree.entity.OperateLogDO;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.lock.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessageService {

    @Autowired
    private MessageDAO messageDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private MessageNoticeDAO messageNoticeDAO;

    @Autowired
    private OperateLogDAO operateLogDAO;

    @Autowired
    private DistributedLock distributedLock;

    private Cache<Long, UserResp> liveUserCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();


    public PageResult<MessageResp> pageQueryMessageList(Integer page, Integer size, UserResp currentUser) {
        List<MessageDO> messageDOList = messageDAO.pageQueryRecentList((page - 1) * size, size);
            if (CollectionUtils.isEmpty(messageDOList)) return new PageResult<>(Collections.emptyList(), 0);

        Set<Long> userIdSet = messageDOList.stream().map(MessageDO::getSendUserId).collect(Collectors.toSet());
        // 后面刷新偏移量需要依赖当前用户id的remark字段，这里一并查出来，避免DB再查一次当前用户
        userIdSet.add(currentUser.getId());
        List<UserDO> userDOList = userDAO.batchQueryByIdList(Lists.newArrayList(userIdSet));
        ImmutableMap<Long, UserDO> userIdMap = Maps.uniqueIndex(userDOList, UserDO::getId);

        Set<Long> relayUserId = messageDOList.stream().map(MessageDO::getId).collect(Collectors.toSet());
        List<MessageNoticeDO> messageNoticeList = messageNoticeDAO.queryByMessageIdList(MessageTypeEnum.MESSAGE_MENTION.getType(), Lists.newArrayList(relayUserId), currentUser.getId());
        // 这里暂时一条消息只支持通知一个用户
        ImmutableMap<Long, MessageNoticeDO> messageIdNoticeMap = Maps.uniqueIndex(messageNoticeList, MessageNoticeDO::getMessageId);

        List<MessageResp> messageRespList = messageDOList.stream().map(messageDO -> {
            MessageResp messageResp = new MessageResp();
            messageResp.setId(messageDO.getId());
            messageResp.setContent(messageDO.getContent());
            messageResp.setCreateTime(messageDO.getCreateTime());
            messageResp.setUserName(userIdMap.get(messageDO.getSendUserId()).getName());
            messageResp.setAvatar(userIdMap.get(messageDO.getSendUserId()).getAvatar());
            messageResp.setUserId(userIdMap.get(messageDO.getSendUserId()).getId());

            MessageNoticeDO messageNoticeDO = messageIdNoticeMap.get(messageDO.getId());
            if (Objects.nonNull(messageNoticeDO)) {
                messageResp.setMentionedUserId(messageNoticeDO.getUserId());
            }
            return messageResp;
        }).collect(Collectors.toList());

        Integer total = messageDAO.count();

        refreshLiveUser(currentUser);
        // 刷新已读最新消息的偏移量
        refreshReadMsgOffset(userIdMap.get(currentUser.getId()), CollectionUtils.isEmpty(messageDOList) ? 0 : messageRespList.get(0).getId());

        return new PageResult<>(messageRespList, total);
    }

    // 这里存在并发
    /**
     * 例如：当前偏移量为1
     * T1 读取偏移量1，更新为2，sleep 未获取分布式锁
     * T2 读取便宜量1，更新为3，更新为3。
     * <p>
     * T1 notified 获取分布式锁，更新为2。
     * <p>
     * 影响：预期为3，实际为2.
     * 所以这里使用Double Check
     */

    private void refreshReadMsgOffset(UserDO userDO, long msgId) {
        if (StringUtils.isEmpty(userDO.getRemark())) return ;
        UserRemarkBO userRemarkBO = JSON.parseObject(userDO.getRemark(), UserRemarkBO.class);
        if (userRemarkBO.getMsgOffsetId() >= msgId) return ;

        String key = LockKeyConstants.UPDATE_MSG_OFFSET_PREFIX + userDO.getId();
        Boolean locked = distributedLock.lock(key, 10, TimeUnit.SECONDS);
        if (! locked) return ;
        try {
            // double check
            userDO = userDAO.getById(userDO.getId());
            userRemarkBO = JSON.parseObject(userDO.getRemark(), UserRemarkBO.class);
            if (userRemarkBO.getMsgOffsetId() >= msgId) return ;
            userRemarkBO.setMsgOffsetId(msgId);
            userDAO.updateRemark(userDO.getId(), JSON.toJSONString(userRemarkBO));
        } finally {
            distributedLock.unlock(key);
        }
    }


    private void refreshLiveUser(UserResp currentUser) {
        liveUserCache.put(currentUser.getId(), currentUser);
    }

    @Transactional
    public Boolean sendMessage(String content, Long replyMessageId, Long userId) {
        MessageDO messageDO = new MessageDO();
        messageDO.setContent(content);
        messageDO.setSendUserId(userId);

        messageDAO.insert(messageDO);

        // 通知所有用户，类似写扩散，这里可能存在性能瓶颈，现在用户量不大，暂时这样处理
//        notifyAllUser(messageDO.getId());

        //@消息通知
        if (Objects.isNull(replyMessageId)) return true;
        MessageNoticeDO messageNoticeDO = new MessageNoticeDO();

        //messageDO.getId()获取刚插入的id
        messageNoticeDO.setMessageId(messageDO.getId());
        Long relayUserId = messageDAO.getSendUserIdByMessageId(replyMessageId);
        messageNoticeDO.setUserId(relayUserId);
        messageNoticeDO.setType(MessageTypeEnum.MESSAGE_MENTION.getType());
        messageNoticeDAO.insert(messageNoticeDO);
        return true;
    }

    private void notifyAllUser(Long messageId) {
        List<Long> userDOList = userDAO.listAllUserId();

        List<MessageNoticeDO> messageNoticeDOList = userDOList.stream().map(userId -> {
            MessageNoticeDO messageNoticeDO = new MessageNoticeDO();
            messageNoticeDO.setMessageId(messageId);
            messageNoticeDO.setUserId(userId);
            return messageNoticeDO;
        }).collect(Collectors.toList());

        messageNoticeDAO.batchInsert(messageNoticeDOList);
    }

    public List<UserResp> getLiveUserCache() {
        return Lists.newArrayList(liveUserCache.asMap().values());
    }

    public Integer getRecentMessageCount(Long userId) {
        UserDO userDO = userDAO.getById(userId);
        String remark = userDO.getRemark();
        if (StringUtils.isEmpty(remark)) {
            // 兼容老逻辑, 不存在就展示24内消息
            if (operateLogDAO.countRecentOpenChatLog(userId, OperateTypeEnum.CHAT.getCode()) > 0) return 0;
            return messageDAO.getRecentMessageCount();
        }
        UserRemarkBO userRemarkBO = JSON.parseObject(remark, UserRemarkBO.class);
        return messageDAO.countAfterId(userId, userRemarkBO.getMsgOffsetId());
    }


    @Autowired
    private TransactionTemplate transactionTemplate;

    public Boolean openChat(Long id) {
        String key = LockKeyConstants.OPEN_CHAT + id;
        Boolean lock = distributedLock.lock(key, 10, TimeUnit.SECONDS);
        if (! lock) {
            log.warn("{} 并发Chat日志写入", id);
            return false;
        }
        try {
            UserDO userDO = userDAO.getById(id);
            Long messageId = messageDAO.getRecentMessageId();
            OperateLogDO operateLogDO = new OperateLogDO();
            operateLogDO.setType(OperateTypeEnum.CHAT.getCode());
            operateLogDO.setUserId(id);
            String remark = userDO.getRemark();
            UserRemarkBO userRemarkBO = StringUtils.isEmpty(remark) ? new UserRemarkBO() : JSON.parseObject(remark, UserRemarkBO.class);
            userRemarkBO.setMsgOffsetId(messageId);
            return transactionTemplate.execute((status) -> {
                operateLogDAO.insert(operateLogDO);
                userDAO.updateRemark(id, JSON.toJSONString(userRemarkBO));
                return true;
            });
        } finally {
            distributedLock.unlock(key);
        }
    }
}
