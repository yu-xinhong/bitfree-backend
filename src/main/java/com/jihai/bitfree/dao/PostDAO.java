package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.PostDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PostDAO {

    Integer count();

    List<PostDO> pageQuery(@Param("start") Integer start,@Param("size") Integer size);

    List<PostDO> queryByIdList(@Param("topPostIdList") List<Long> topPostIdList);

    PostDO getById(@Param("id") Long id);

    List<PostDO> getByIdList(@Param("idList") List<Long> idList);

    void insert(PostDO postDO);
}
