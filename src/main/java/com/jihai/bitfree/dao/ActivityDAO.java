package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.ActivityDO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface ActivityDAO {

    ActivityDO getRecent();

    ActivityDO queryActivity(Long activity);

    int updateStock(@Param("id") Long id, @Param("count") Integer count);

    List<ActivityDO> queryByIds(Collection<Long> activities);
}
