package com.jihai.bitfree.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.dao.TaskBoardDAO;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.dto.req.TaskBoardReq;
import com.jihai.bitfree.dto.resp.TaskBoardResp;
import com.jihai.bitfree.entity.TaskBoardDO;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.enums.TaskStatusEnum;
import com.jihai.bitfree.enums.TaskStrategyEnum;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.service.strategy.BaseTaskBoardParam;
import com.jihai.bitfree.service.strategy.TaskBoardStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@Service
@Slf4j
public class TaskBoardService {
    @Autowired
    private TaskBoardDAO taskBoardDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ConfigService configService;

    @Autowired
    private List<TaskBoardStatus<BaseTaskBoardParam>> taskStatusHandler;

    private List<Long> taskBoardAdminUserIdList = Lists.newArrayList();
    private Map<TaskStatusEnum, TaskBoardStatus<BaseTaskBoardParam>> taskStatusHandlerMap;

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

        taskStatusHandlerMap = taskStatusHandler.stream().collect(Collectors.toMap(TaskBoardStatus::supportStatus, i -> i,
                (b, a) -> {throw new BusinessException("看板状态变更策略存在冲突: " + b.supportStatus());}));
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
            taskBoardResp.setReceiveTime(taskBoardDO.getReceiveTime());
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

    @Transactional(rollbackFor = Exception.class, isolation = READ_COMMITTED)
    public Boolean strategyChangeStatus(Long userId, Integer taskId, TaskStrategyEnum strategyStatus){
        TaskBoardStatus<BaseTaskBoardParam> handler = taskStatusHandlerMap.get(strategyStatus.status);
        //  兜底
        if (ObjectUtils.isEmpty(handler)){
            throw new BusinessException("不支持的操作: " + strategyStatus);
        }
        TaskBoardDO taskBoardDO = taskBoardDAO.getTaskByTaskId(taskId, handler.beforeStatus().getStatus());
        if (ObjectUtils.isEmpty(taskBoardDO)) {
            throw new BusinessException("任务不存在");
        }

        //  由于事务的限制, 以下操作不方便在抽象类上进行
        boolean isSuccess = handler.doChange(new BaseTaskBoardParam(userId, taskId));
        // 并发时提示
        if (!isSuccess){
            throw new BusinessException("未找到指定任务,请刷新后再试");
        }

        return true;
    }

}
