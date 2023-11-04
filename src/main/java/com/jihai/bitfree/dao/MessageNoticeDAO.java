package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.MessageNoticeDO;

import java.util.List;

public interface MessageNoticeDAO {

    void batchInsert(List<MessageNoticeDO> messageNoticeDOList);

    List<MessageNoticeDO> queryByMessageIdListAll(Integer type, List<Long> notificationIdList);

    List<MessageNoticeDO> queryByMessageIdList(Integer type, List<Long> notificationIdList, Long userId);

    Integer count(Long userId, Integer type);

    void insert(MessageNoticeDO messageNoticeDO);
}
