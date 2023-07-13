package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.MessageDO;

import java.util.List;

public interface MessageDAO {

    List<MessageDO> pageQueryRecentList(Integer start, Integer size);

    Integer count();

    void insert(MessageDO messageDO);
}
