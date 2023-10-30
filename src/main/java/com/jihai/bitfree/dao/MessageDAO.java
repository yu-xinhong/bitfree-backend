package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.MessageDO;

import java.util.List;

public interface MessageDAO {

    List<MessageDO> pageQueryRecentList(Integer start, Integer size);

    Integer count();

    Integer insert(MessageDO messageDO);

    Integer getRecentMessageCount();

    Integer countAfterId(Long userId, Long id);

    Long getRecentMessageId();

    Long getByMessageId(Long id);
}
