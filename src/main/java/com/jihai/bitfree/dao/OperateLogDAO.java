package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.OperateLogDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface OperateLogDAO {
    void insert(OperateLogDO operateLogDO);

    Integer queryByUserIdAndType(@Param("userId") Long id,@Param("type") Integer type);

    Integer countRecentOpenChatLog(Long userId, Integer type);

    Integer countLoginRecord(Long userId, Integer type, Date date);
}
