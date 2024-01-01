package com.jihai.bitfree.service.strategy;

import com.google.common.collect.Lists;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.dao.TaskBoardDAO;
import com.jihai.bitfree.entity.TaskBoardDO;
import com.jihai.bitfree.enums.OperateTypeEnum;
import com.jihai.bitfree.enums.TaskStatusEnum;
import com.jihai.bitfree.enums.TaskStrategyEnum;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.service.CoinsService;
import com.jihai.bitfree.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@Component
public class CompleteTaskStatus extends TaskBoardStatus<BaseTaskBoardParam>{

    @Autowired
    private ConfigService configService;


    @Autowired
    private TaskBoardDAO taskBoardDAO;

    @Autowired
    private CoinsService coinsService;

    public CompleteTaskStatus() {
        super(TaskStatusEnum.DOING, TaskStrategyEnum.COMPLETE);
    }

    private final List<Long> taskBoardAdminUserIdList = Lists.newArrayList();

    @PostConstruct
    public void initCompleteUserList() {
        String userIdConfigVal = null;
        try {
            userIdConfigVal = configService.getByKey(Constants.TASK_COMPLETE_USER_LIST);
        } catch (BusinessException e) {
            throw new BusinessException("未找到配置: " + "TASK_COMPLETE_USER_LIST");
        }
        if (StringUtils.isEmpty(userIdConfigVal)) return ;
        List<String> userIdStrList = Arrays.asList((userIdConfigVal.split(",")));
        taskBoardAdminUserIdList.addAll(userIdStrList.stream().map(Long::valueOf).collect(Collectors.toList()));
    }


    @Override
    @Transactional(rollbackFor = Exception.class, isolation = READ_COMMITTED)
    public boolean doChange(BaseTaskBoardParam param) {
        if (! taskBoardAdminUserIdList.contains(param.getUserId())) {
            throw new BusinessException("您没有完成任务的权限");
        }

        TaskBoardDO taskBoardDO = taskBoardDAO.getTaskByTaskId(param.getTaskId(), beforeStatus().getStatus());
        int updateColumn = taskBoardDAO.updateByIdAndStatus(param.getTaskId(), taskBoardDO.getUserId(), beforeStatus().getStatus(), supportStatus().getStatus());
        if (updateColumn == 1){
            coinsService.incrementCoins(taskBoardDO.getUserId(), taskBoardDO.getCoins(), OperateTypeEnum.TASK_COINS);
            return true;
        }else {
            return false;
        }
    }
}
