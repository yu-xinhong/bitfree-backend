package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.ReplyNoticeDO;
import org.apache.ibatis.annotations.Param;

public interface ReplyNoticeDAO {

    void insert(ReplyNoticeDO replyNoticeDO);

    Integer countReplyCount(@Param("userId") Long userId);

    void updateStatus(@Param("userId") Long userId);

    void deleted(@Param("postId") Long postId);

    void deletedByReplyId(@Param("replyId") Long id);
}
