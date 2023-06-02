package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.ReplyDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReplyDAO {

    List<ReplyDO> queryByPostIdList(@Param("postIdList") List<Long> postIdList);

    List<ReplyDO> getByPostId(@Param("postId") Long postId);

    Long insert(ReplyDO replyDO);

    List<ReplyDO> getBySendUserId(@Param("userId") Long userId, @Param("start") Integer start, @Param("size") Integer size);

    Integer count(@Param("userId") Long userId);

    List<ReplyDO> queryByIdList(@Param("replyIdList") List<Long> replyIdList);

    ReplyDO getById(Long id);
}
