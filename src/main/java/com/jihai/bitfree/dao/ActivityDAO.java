package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.ActivityDO;
import org.apache.ibatis.annotations.Param;

public interface ActivityDAO {

    ActivityDO getRecent();

    ActivityDO queryActivity(Long activity);

    int updateStock(@Param("id") Long id, @Param("count") Integer count);
}
