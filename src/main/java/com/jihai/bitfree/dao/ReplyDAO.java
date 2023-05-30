package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.ReplyDO;

import java.util.List;

public interface ReplyDAO {

    List<ReplyDO> countByPostIdList(List<Long> postIdList);

    List<ReplyDO> getByPostId(Long id);
}
