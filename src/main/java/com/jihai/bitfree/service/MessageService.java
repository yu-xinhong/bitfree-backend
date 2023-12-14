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
import com.jihai.bitfree.constants.CoinsDefinitions;
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
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.lock.DistributedLock;
import com.jihai.bitfree.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ExecutionException;
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

    @Autowired
    private OperationLogService operationLogService;

    private Cache<Long, UserResp> liveUserCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();

    // 这里前端js 1分钟发起一次心跳，但是这里5秒考虑到网络波动
    private Cache<Long, Heartbeat> heartbeatCache = CacheBuilder.newBuilder().expireAfterAccess(65, TimeUnit.SECONDS).build();

    class Heartbeat {
        private Integer count;
        private Long timestamp;

        public Heartbeat(Integer count, Long timestamp) {
            this.count = count;
            this.timestamp = timestamp;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
    }

    public PageResult<MessageResp> pageQueryMessageList(Integer page, Integer size, UserResp currentUser) {
        List<MessageDO> messageDOList = messageDAO.pageQueryRecentList((page - 1) * size, size);
        if (CollectionUtils.isEmpty(messageDOList)) return new PageResult<>(Collections.emptyList(), 0);

        Set<Long> targetMessageIdSet = messageDOList.stream().filter(messageDO -> messageDO.getTargetMessageId() != null).map(MessageDO::getTargetMessageId).collect(Collectors.toSet());
        List<MessageDO> targetMessageIdList = CollectionUtils.isEmpty(targetMessageIdSet) ? Lists.newArrayList() : messageDAO.queryByTargetMessageIdList(Lists.newArrayList(targetMessageIdSet));
        ImmutableMap<Long, MessageDO> targetMessageIdMap = Maps.uniqueIndex(targetMessageIdList, MessageDO::getId);
        Set<Long> targetUserIdSet = targetMessageIdList.stream().map(MessageDO::getSendUserId).collect(Collectors.toSet());

        Set<Long> userIdSet = messageDOList.stream().map(MessageDO::getSendUserId).collect(Collectors.toSet());
        Set<Long> messageIdSet = messageDOList.stream().map(MessageDO::getId).collect(Collectors.toSet());
        List<MessageNoticeDO> messageNoticeList = messageNoticeDAO.queryByMessageIdListAll(MessageTypeEnum.MESSAGE_MENTION.getType(), Lists.newArrayList(messageIdSet));
        // 后面刷新偏移量需要依赖当前用户id的remark字段，这里一并查出来，避免DB再查一次当前用户
        userIdSet.add(currentUser.getId());
        userIdSet.addAll(targetUserIdSet);
        userIdSet.addAll(messageNoticeList.stream().map(MessageNoticeDO::getUserId).collect(Collectors.toSet()));
        List<UserDO> userDOList = userDAO.batchQueryByIdList(Lists.newArrayList(userIdSet));
        ImmutableMap<Long, UserDO> userIdMap = Maps.uniqueIndex(userDOList, UserDO::getId);

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

            MessageDO targetMessageDO = targetMessageIdMap.get(messageDO.getTargetMessageId());
            if(Objects.nonNull(targetMessageDO)){
                messageResp.setMentionedContent(targetMessageDO.getContent());
            }

            MessageNoticeDO messageNoticeDO = messageIdNoticeMap.get(messageDO.getId());
            if (Objects.nonNull(messageNoticeDO)) {
                messageResp.setMentionedUserId(messageNoticeDO.getUserId());
                messageResp.setMentionedUserName(userIdMap.get(messageNoticeDO.getUserId()).getName());
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
    public Boolean sendMessage(String content, Long replyMessageId, Long userId, Long atUser) {
        MessageDO messageDO = new MessageDO();
        messageDO.setContent(content);
        messageDO.setSendUserId(userId);
        messageDO.setTargetMessageId(replyMessageId);

        messageDAO.insert(messageDO);

        if (atUser != null) {
            MessageNoticeDO messageNoticeDO = new MessageNoticeDO();
            messageNoticeDO.setType(MessageTypeEnum.MESSAGE_MENTION.getType());
            messageNoticeDO.setUserId(atUser);
            messageNoticeDO.setMessageId(messageDO.getId());
            messageNoticeDAO.insert(messageNoticeDO);
            return true;
        }

        // 通知所有用户，类似写扩散，这里可能存在性能瓶颈，现在用户量不大，暂时这样处理
//        notifyAllUser(messageDO.getId());

        //  @消息通知

        /*
         * 存在删除消息与回复消息的并发问题, timeline:
         * 1. A发送回复评论ID.1的ID.2消息
         * 2. B删除评论ID.1(在A执行#messageDAO.getSendUserIdByMessageId前)
         * OR
         * 1. B删除评论ID.1
         * 2. A在获取最新消息前发送回复评论ID.1的ID.2消息
         *
         * GAP:
         * 回复消息: 在删除消息前到删除成功消息后的第一次获取最新消息时
         * #messageDAO.getSendUserIdByMessageId : 在删除消息成功后
         *
         * @note: 成功获取relayUserId后删除消息不用做处理, 在查消息时查不到对应的message时不会显示被回复消息
         */
        if (replyMessageId == null) return true;
        Long relayUserId = messageDAO.getSendUserIdByMessageId(replyMessageId);
        if (relayUserId == null) return true;
        MessageNoticeDO messageNoticeDO = new MessageNoticeDO();

        //messageDO.getId()获取刚插入的id
        messageNoticeDO.setMessageId(messageDO.getId());
        messageNoticeDO.setUserId(relayUserId);
        messageNoticeDO.setType(MessageTypeEnum.MESSAGE_MENTION.getType());
        messageNoticeDAO.insert(messageNoticeDO);
        return true;
    }

    @Transactional
    public Boolean deleteMessage(Long messageId) {
        messageDAO.delete(messageId);
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

    public Boolean heartbeat(Long userId) {
        Heartbeat heartbeat;
        try {
            heartbeat = heartbeatCache.get(userId, () -> new Heartbeat(0, null));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        // 这里可能存在并发，暂不考虑
        long currentSystemTimestamp = System.currentTimeMillis();
        if (heartbeat.getTimestamp() != null && currentSystemTimestamp - heartbeat.getTimestamp() < 59 * 1000) {
            heartbeatCache.invalidate(userId);
            throw new BusinessException("无效心跳");
        }

        heartbeat.setCount(heartbeat.getCount() + 1);
        heartbeat.setTimestamp(currentSystemTimestamp);

        // 超过每日上限
        if (isOverUp(userId)) {
            heartbeatCache.invalidate(userId);
            return true;
        }

        if (heartbeat.getCount() < RandomUtils.nextInt(5,10)) return true;

        log.info("userId {} live appraise coins ", userId);
        heartbeatCache.invalidate(userId);
        transactionTemplate.execute((status) -> {
            userDAO.incrementCoins(userId, 1);
            operationLogService.asynSaveOperateLog(userId, OperateTypeEnum.LIVE_COINS);
            return null;
        });

        return true;
    }

    private boolean isOverUp(Long userId) {
        return operateLogDAO.countLoginRecord(userId, OperateTypeEnum.LIVE_COINS.getCode(), DateUtils.formatDay(new Date())) >= CoinsDefinitions.LIVE_DAY_OVER_COINS;
    }
}
