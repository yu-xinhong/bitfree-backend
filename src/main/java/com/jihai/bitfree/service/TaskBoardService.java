package com.jihai.bitfree.service;

import cn.hutool.core.util.ObjUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.base.enums.OperateTypeEnum;
import com.jihai.bitfree.base.enums.TaskStatusEnum;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.constants.LockKeyConstants;
import com.jihai.bitfree.dao.TaskBoardDAO;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.dto.req.TaskBoardReq;
import com.jihai.bitfree.dto.resp.TaskBoardResp;
import com.jihai.bitfree.entity.TaskBoardDO;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.lock.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskBoardService {
    @Autowired
    private TaskBoardDAO taskBoardDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private DistributedLock distributedLock;
    @Autowired
    private ConfigService configService;

    private List<Long> taskBoardAdminUserIdList = Lists.newArrayList();

    @PostConstruct
    public void initCompleteUserList() {
        String userIdConfigVal = null;
        try {
            userIdConfigVal = configService.getByKey(Constants.TASK_COMPLETE_USER_LIST);
        } catch (BusinessException e) {
        }
        if (StringUtils.isEmpty(userIdConfigVal)) return ;
        List<String> userIdStrList = Arrays.asList((userIdConfigVal.split(",")));
        taskBoardAdminUserIdList.addAll(userIdStrList.stream().map(Long::valueOf).collect(Collectors.toList()));
    }

    public PageResult<TaskBoardResp> pageQueryTaskBoardList(Long userId, TaskBoardReq taskBoardReq){
        int start = (taskBoardReq.getPage() - 1) * taskBoardReq.getSize();
        List<TaskBoardDO> taskBoardDOList = taskBoardDAO.pageQueryTaskBoardListByStatus(taskBoardReq.getStatus(), start, taskBoardReq.getSize());
        if (CollectionUtils.isEmpty(taskBoardDOList)) return new PageResult<>(Collections.emptyList(), 0);

        Set<Long> taskBoardUserIdSet = taskBoardDOList.stream().filter(taskBoardDO -> taskBoardDO.getUserId() != null).map(TaskBoardDO::getUserId).collect(Collectors.toSet());
        List<UserDO> userDOList = CollectionUtils.isEmpty(taskBoardUserIdSet) ? Lists.newArrayList() : userDAO.batchQueryByIdList(Lists.newArrayList(taskBoardUserIdSet));
        ImmutableMap<Long, UserDO> userIdMap = Maps.uniqueIndex(userDOList, UserDO::getId);
        List<TaskBoardResp> taskBoardRespList = taskBoardDOList.stream().map(taskBoardDO -> {
            TaskBoardResp taskBoardResp = new TaskBoardResp();
            taskBoardResp.setId(taskBoardDO.getId());
            taskBoardResp.setContent(taskBoardDO.getContent());
            taskBoardResp.setCoins(taskBoardDO.getCoins());
            taskBoardResp.setTaskLevel(taskBoardDO.getLevel());
            taskBoardResp.setStatus(taskBoardDO.getStatus());
            taskBoardResp.setRemark(taskBoardDO.getRemark());
            taskBoardResp.setCreateTime(taskBoardDO.getCreateTime());
            taskBoardResp.setCompleteFlag(taskBoardAdminUserIdList.contains(userId));
            if(taskBoardDO.getUserId() != null){
                taskBoardResp.setUserName(userIdMap.get(taskBoardDO.getUserId()).getName());
                taskBoardResp.setAvatar(userIdMap.get(taskBoardDO.getUserId()).getAvatar());
                taskBoardResp.setUserId(userIdMap.get(taskBoardDO.getUserId()).getId());
            }
            return taskBoardResp;
        }).collect(Collectors.toList());

        Integer total = taskBoardDAO.countByStatus(taskBoardReq.getStatus());

        return new PageResult<>(taskBoardRespList, total);
    }

    public Boolean applyForTask(Long userId, Integer taskId) {
        List<TaskBoardDO> taskByTaskUserList = taskBoardDAO.getTaskByTaskUserId(userId, TaskStatusEnum.DOING.getStatus());
        if (ObjUtil.isNotEmpty(taskByTaskUserList) && taskByTaskUserList.size() >= 3) {
            throw new BusinessException("您处理中的任务大于3个,请尽快完成后再申领噢～");
        }
        this.updateTask(userId, taskId, TaskStatusEnum.TODO.getStatus(), TaskStatusEnum.DOING.getStatus());
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean completeTask(Long userId, Integer taskId) {
        if (! taskBoardAdminUserIdList.contains(userId)) {
            throw new BusinessException("指定用户才可操作完成");
        }
        TaskBoardDO taskBoardDO = this.updateTask(userId, taskId, TaskStatusEnum.DOING.getStatus(), TaskStatusEnum.DONE.getStatus());
        userDAO.incrementCoins(userId, taskBoardDO.getCoins());
        operationLogService.asynSaveOperateLog(userId, OperateTypeEnum.TASK_COINS);
        return true;
    }

    public Boolean cancelTask(Integer taskId) {
        // 如果是取消任务,需要把申领用户重置为null
        this.updateTask(null, taskId, TaskStatusEnum.DOING.getStatus(), TaskStatusEnum.TODO.getStatus());
        return true;
    }

    /**
     * 修改任务
     * @param userId 修改用户id
     * @param taskId 任务id
     * @param beforeTaskStatus 修改前状态
     * @param afterTaskStatus 修改后状态
     * @return 任务详情
     */
    private TaskBoardDO updateTask(Long userId, Integer taskId, Integer beforeTaskStatus, Integer afterTaskStatus) {
        String lockKey = LockKeyConstants.UPDATE_TASK + taskId;
        Boolean locked = distributedLock.lock(lockKey, 1, TimeUnit.MINUTES);
        if (! locked) {
            throw new BusinessException("系统繁忙,请稍后再试");
        }
        TaskBoardDO taskBoardDO;
        try {
            taskBoardDO = taskBoardDAO.getTaskByTaskId(taskId, beforeTaskStatus);
            if (ObjUtil.isNull(taskBoardDO)) {
                throw new BusinessException("任务不存在");
            }
            // 如果是完成任务,则取申领人的id
            taskBoardDO.setUserId(TaskStatusEnum.DONE.getStatus().equals(afterTaskStatus) ? taskBoardDO.getUserId() : userId);
            taskBoardDO.setStatus(afterTaskStatus);
            taskBoardDAO.updateTaskBoard(taskBoardDO);
        } finally {
            distributedLock.unlock(lockKey);
        }
        return taskBoardDO;
    }
}
