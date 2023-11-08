package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.TaskBoardDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskBoardDAO {

    List<TaskBoardDO> pageQueryTaskBoardListByStatus(Integer status, Integer start, Integer size);

    Integer count();

    void insert(TaskBoardDO taskBoardDO);

    void updateTime(@Param("id") Long id);

    void deleted(@Param("id") Long id);

}
