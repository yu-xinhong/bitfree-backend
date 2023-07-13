package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.MessageNoticeDO;

import java.util.List;

public interface MessageNoticeDAO {

    void batchInsert(List<MessageNoticeDO> messageNoticeDOList);
}
