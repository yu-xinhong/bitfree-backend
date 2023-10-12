package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.CollectDO;

import java.util.List;

public interface CollectDAO {

    void insert(CollectDO collectDO);

    Integer hasCollect(Long postId, Long userId, Integer type);

    Integer delete(Long postId, Long userId, Integer type);

    List<CollectDO> pageQuery(Long userId, Integer start, Integer limit, Integer type);

    Integer countTotal(Long userId, Integer type);
}
