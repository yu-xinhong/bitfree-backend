package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.TaskBoardDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskBoardDAO {

    List<TaskBoardDO> pageQueryTaskBoardListByStatus(Integer status, Integer start, Integer size);

    Integer count();

    Integer countByStatus(Integer status);

    void insert(TaskBoardDO taskBoardDO);

    void updateTime(@Param("id") Long id);

    void deleted(@Param("id") Long id);

    TaskBoardDO getTaskByTaskId(Integer id,Integer status);

    List<TaskBoardDO> getTaskByTaskUserId(Long userId,Integer status);

    Integer updateTaskBoard(TaskBoardDO taskBoardDO);
}
