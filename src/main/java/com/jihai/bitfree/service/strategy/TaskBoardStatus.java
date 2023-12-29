package com.jihai.bitfree.service.strategy;

import com.jihai.bitfree.enums.TaskStatusEnum;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;


public abstract class TaskBoardStatus<X extends BaseTaskBoardParam> {

    /**
     * 支持的状态变更类型
     */
    private final TaskStatusEnum strategyStatus;

    private final TaskStatusEnum beforeStatus;

    /**
     * 指定状态变更的类型, before -> target
     */
    protected TaskBoardStatus(TaskStatusEnum beforeStatus, TaskStatusEnum strategyStatus) {
        this.beforeStatus = beforeStatus;
        this.strategyStatus = strategyStatus;
    }

    @Transactional(rollbackFor = Exception.class, isolation = READ_COMMITTED)
    public abstract boolean doChange(X param);

    public TaskStatusEnum supportStatus(){
        return this.strategyStatus;
    }

    public TaskStatusEnum beforeStatus() {
        return this.beforeStatus;
    }


}
