package com.jihai.bitfree.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.dao.TaskBoardDAO;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.dto.resp.TaskBoardResp;
import com.jihai.bitfree.entity.TaskBoardDO;
import com.jihai.bitfree.entity.UserDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskBoardService {
    @Autowired
    private TaskBoardDAO taskBoardDAO;

    @Autowired
    private UserDAO userDAO;

    public PageResult<TaskBoardResp> pageQueryTaskBoardList(Integer status, Integer page, Integer size){
        System.out.println(1);
        List<TaskBoardDO> taskBoardDOList = taskBoardDAO.pageQueryTaskBoardListByStatus(status, (page - 1) * size, size);
        if (CollectionUtils.isEmpty(taskBoardDOList)) return new PageResult<>(Collections.emptyList(), 0);

        Set<Long> taskBoardUserIdSet = taskBoardDOList.stream().filter(taskBoardDO -> taskBoardDO.getUserId() != null).map(TaskBoardDO::getUserId).collect(Collectors.toSet());
        List<UserDO> userDOList = userDAO.batchQueryByIdList(Lists.newArrayList(taskBoardUserIdSet));
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

        Integer total = taskBoardDAO.count();

        return new PageResult<>(taskBoardRespList, total);
    }
}
