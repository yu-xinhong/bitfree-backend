package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.CheckInDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface CheckInDAO {

    Integer getByCurrentDay(@Param("userId") Long userId, @Param("date") Date date);

    Boolean insert(@Param("userId") Long userId, @Param("date") Date date);

    List<CheckInDO> listRecentWeek(Long userId);
}
