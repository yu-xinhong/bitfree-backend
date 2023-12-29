package com.jihai.bitfree.enums;

public enum TaskStrategyEnum {

    APPLY(TaskStatusEnum.DOING),

    SUBMIT(TaskStatusEnum.SUBMIT),

    CANCEL(TaskStatusEnum.TODO),

    COMPLETE(TaskStatusEnum.DONE)

    ;


    public final TaskStatusEnum status;

    TaskStrategyEnum(TaskStatusEnum status) {
        this.status = status;
    }
}
