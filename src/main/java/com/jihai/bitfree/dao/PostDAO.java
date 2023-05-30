package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.PostDO;

import java.util.List;

public interface PostDAO {

    Integer count();

    List<PostDO> pageQuery(Integer page, Integer size);
}
