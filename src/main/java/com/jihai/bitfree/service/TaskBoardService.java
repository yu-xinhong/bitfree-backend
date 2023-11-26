package com.jihai.bitfree.service;

import cn.hutool.core.util.ObjUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.base.enums.TaskStatusEnum;
import com.jihai.bitfree.dao.TaskBoardDAO;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.dto.resp.TaskBoardResp;
import com.jihai.bitfree.entity.TaskBoardDO;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskBoardService {
    @Autowired
    private TaskBoardDAO taskBoardDAO;

    @Autowired
    private UserDAO userDAO;

    private final ReentrantLock reentrantLock = new ReentrantLock(true);

    public PageResult<TaskBoardResp> pageQueryTaskBoardList(Integer status, Integer page, Integer size){
        List<TaskBoardDO> taskBoardDOList = taskBoardDAO.pageQueryTaskBoardListByStatus(status, (page - 1) * size, size);
        if (CollectionUtils.isEmpty(taskBoardDOList)) return new PageResult<>(Collections.emptyList(), 0);

        Set<Long> taskBoardUserIdSet = taskBoardDOList.stream().filter(taskBoardDO -> taskBoardDO.getUserId() != null).map(TaskBoardDO::getUserId).collect(Collectors.toSet());
        List<UserDO> userDOList = CollectionUtils.isEmpty(taskBoardUserIdSet) ? Lists.newArrayList() : userDAO.batchQueryByIdList(Lists.newArrayList(taskBoardUserIdSet));
        ImmutableMap<Long, UserDO> userIdMap = Maps.uniqueIndex(userDOList, UserDO::getId);

        List<TaskBoardResp> taskBoardRespList = taskBoardDOList.stream().map(taskBoardDO -> {
            TaskBoardResp taskBoardResp = new TaskBoardResp();
            taskBoardResp.setId(taskBoardDO.getId());
            taskBoardResp.setContent(taskBoardDO.getContent());
            taskBoardResp.setCoins(taskBoardDO.getCoins());
            taskBoardResp.setStatus(taskBoardDO.getStatus());
            taskBoardResp.setCreateTime(taskBoardDO.getCreateTime());
            if(taskBoardDO.getUserId() != null){
                taskBoardResp.setUserName(userIdMap.get(taskBoardDO.getUserId()).getName());
                taskBoardResp.setAvatar(userIdMap.get(taskBoardDO.getUserId()).getAvatar());
                taskBoardResp.setUserId(userIdMap.get(taskBoardDO.getUserId()).getId());
            }
            return taskBoardResp;
        }).collect(Collectors.toList());

        Integer total = taskBoardDAO.countByStatus(status);

        return new PageResult<>(taskBoardRespList, total);
    }

    public String applyForTask(Long userId, Integer taskId) {
        List<TaskBoardDO> taskByTaskUserList = taskBoardDAO.getTaskByTaskUserId(userId, TaskStatusEnum.DOING.getStatus());
        if (ObjUtil.isNotEmpty(taskByTaskUserList) && taskByTaskUserList.size() >= 3) {
            throw new BusinessException("您处理中的任务大于3个,请尽快完成后再申领噢～");
        }
        TaskBoardDO taskBoardDO = taskBoardDAO.getTaskByTaskId(taskId, TaskStatusEnum.TODO.getStatus());
        if (ObjUtil.isNull(taskBoardDO)) {
            throw new BusinessException("任务不存在或已被申领");
        }
        this.updateTask(userId, taskBoardDO, TaskStatusEnum.DOING.getStatus());
        return "success";
    }

    @Transactional(rollbackFor = Exception.class)
    public String completeTask(Long userId, Integer taskId) {
        TaskBoardDO taskBoardDO = taskBoardDAO.getTaskByTaskId(taskId, TaskStatusEnum.DOING.getStatus());
        if (ObjUtil.isNull(taskBoardDO)) {
            throw new BusinessException("任务不存在");
        }
        if (!userId.equals(taskBoardDO.getUserId())) {
            throw new BusinessException("不是您的任务，非法操作将被封禁");
        }
        this.updateTask(userId, taskBoardDO, TaskStatusEnum.DONE.getStatus());
        userDAO.incrementCoins(userId, taskBoardDO.getCoins());
        return "success";
    }

    public String cancelTask(Long userId, Integer taskId) {
        TaskBoardDO taskBoardDO = taskBoardDAO.getTaskByTaskId(taskId, TaskStatusEnum.DOING.getStatus());
        if (ObjUtil.isNull(taskBoardDO)) {
            throw new BusinessException("任务不存在");
        }
        if (!userId.equals(taskBoardDO.getUserId())) {
            throw new BusinessException("不是您的任务，非法操作将被封禁");
        }
        // 用户重置为null,状态修改为待办
        updateTask(null, taskBoardDO, TaskStatusEnum.TODO.getStatus());
        return "success";
    }

    private void updateTask(Long userId, TaskBoardDO taskBoardDO, Integer taskStatus) {
        boolean sucLock;
        try {
            sucLock = reentrantLock.tryLock(1L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new BusinessException("系统繁忙");
        }
        if (!sucLock) {
            throw new BusinessException("系统繁忙,请稍后再试");
        }
        try {
            taskBoardDO.setUserId(userId);
            taskBoardDO.setStatus(taskStatus);
            taskBoardDAO.updateTaskBoard(taskBoardDO);
        } catch (Exception e) {
            log.error("修改task表异常：", e);
            throw new BusinessException("系统异常,请联系管理员");
        } finally {
            reentrantLock.unlock();
        }
    }
}
