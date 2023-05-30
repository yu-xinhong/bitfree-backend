package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.ReplyNoticeDO;

public interface ReplyNoticeDAO {

    void insert(ReplyNoticeDO replyNoticeDO);

    Integer countReplyCount(Long userId);

    void updateStatus(Long userId);
}
