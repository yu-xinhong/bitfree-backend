package com.jihai.bitfree.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.dao.MessageDAO;
import com.jihai.bitfree.dao.MessageNoticeDAO;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.dto.resp.MessageResp;
import com.jihai.bitfree.entity.MessageDO;
import com.jihai.bitfree.entity.MessageNoticeDO;
import com.jihai.bitfree.entity.UserDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageDAO messageDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private MessageNoticeDAO messageNoticeDAO;

    public PageResult<MessageResp> pageQueryMessageList(Integer page, Integer size) {
        List<MessageDO> messageDOList = messageDAO.pageQueryRecentList((page - 1) * size, size);
        if (CollectionUtils.isEmpty(messageDOList)) return new PageResult<>(Collections.emptyList(), 0);

        Set<Long> userIdSet = messageDOList.stream().map(MessageDO::getSendUserId).collect(Collectors.toSet());
        List<UserDO> userDOList = userDAO.batchQueryByIdList(Lists.newArrayList(userIdSet));
        ImmutableMap<Long, UserDO> userIdMap = Maps.uniqueIndex(userDOList, UserDO::getId);

        List<MessageResp> messageRespList = messageDOList.stream().map(messageDO -> {
            MessageResp messageResp = new MessageResp();
            messageResp.setId(messageDO.getId());
            messageResp.setContent(messageDO.getContent());
            messageResp.setCreateTime(messageDO.getCreateTime());
            messageResp.setUserName(userIdMap.get(messageDO.getSendUserId()).getName());
            messageResp.setAvatar(userIdMap.get(messageDO.getSendUserId()).getAvatar());
            messageResp.setUserId(userIdMap.get(messageDO.getSendUserId()).getId());
            return messageResp;
        }).collect(Collectors.toList());

        Integer total = messageDAO.count();

        return new PageResult<>(messageRespList, total);
    }

    @Transactional
    public Boolean sendMessage(String content, Long userId) {
        MessageDO messageDO = new MessageDO();
        messageDO.setContent(content);
        messageDO.setSendUserId(userId);

        messageDAO.insert(messageDO);

        // 通知所有用户，类似写扩散，这里可能存在性能瓶颈，现在用户量不大，暂时这样处理
//        notifyAllUser(messageDO.getId());
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
}
