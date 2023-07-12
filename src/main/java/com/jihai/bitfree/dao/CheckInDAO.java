package com.jihai.bitfree.dao;

import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface CheckInDAO {

    Integer getByCurrentDay(@Param("userId") Long userId, @Param("date") Date date);

    Boolean insert(@Param("userId") Long userId, @Param("date") Date date);
}
