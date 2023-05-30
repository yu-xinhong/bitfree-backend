package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.ReplyDO;

import java.util.List;

public interface ReplyDAO {

    List<ReplyDO> countByPostIdList(List<Long> postIdList);

    List<ReplyDO> getByPostId(Long postId);

    Long insert(ReplyDO replyDO);

    List<ReplyDO> getBySendUserId(Long userId, Integer page, Integer size);

    Integer count(Long userId);

    List<ReplyDO> queryByIdList(List<Long> replyIdList);
}
