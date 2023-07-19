package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.UserLikeDO;

import java.util.List;

public interface UserLikeDAO {

    void insert(UserLikeDO userLikeDO);

    Integer countLike(Long id, Integer type);

    List<UserLikeDO> getLikeList(List<Long> targetIdList, Integer type, Long userId);
}
