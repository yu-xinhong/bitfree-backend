package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.ReplyDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReplyDAO {

    List<ReplyDO> queryByPostIdList(@Param("postIdList") List<Long> postIdList);

    List<ReplyDO> getByPostId(@Param("postId") Long postId);

    Long insert(ReplyDO replyDO);

    List<ReplyDO> pageQueryBySendUserId(@Param("userId") Long userId, @Param("start") Integer start, @Param("size") Integer size);

    Integer countBySendUserId(@Param("userId") Long userId);

    List<ReplyDO> queryByIdList(@Param("replyIdList") List<Long> replyIdList);

    ReplyDO getById(Long id);

    List<ReplyDO> pageQueryByReceiverId(@Param("userId") Long userId, @Param("start") Integer start, @Param("size") Integer size);

    Integer countByReceiverId(@Param("userId") Long userId);

    void deleted(@Param("postId") Long postId);

    void deletedById(Long id);

    void deletedByTargetId(@Param("targetReplyId") Long targetReplyId);
}
