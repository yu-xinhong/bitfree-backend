package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.PostDO;

import java.util.List;

public interface PostDAO {

    Integer count();

    List<PostDO> pageQuery(Integer page, Integer size);

    List<PostDO> queryByIdList(List<Long> topPostIdList);

    PostDO getById(Long id);

    List<PostDO> getByIdList(List<Long> idList);

    void insert(PostDO postDO);
}
