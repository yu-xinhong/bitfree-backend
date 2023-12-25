package com.jihai.bitfree.service.strategy;

import com.jihai.bitfree.dao.TaskBoardDAO;
import com.jihai.bitfree.enums.TaskStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@Component
public class CancelTaskStatus extends TaskBoardStatus<BaseTaskBoardParam>{

    @Autowired
    private TaskBoardDAO taskBoardDAO;

    public CancelTaskStatus() {
        super(TaskStatusEnum.DOING, TaskStatusEnum.CANCEL);
    }


    @Override
    @Transactional(rollbackFor = Exception.class, isolation = READ_COMMITTED)
    public boolean doChange(BaseTaskBoardParam param) {
        int updateColumn = taskBoardDAO.updateByIdAndStatus(param.getTaskId(), param.getUserId(), beforeStatus().getStatus(), supportStatus().getStatus());
        return updateColumn == 1;
    }

}
