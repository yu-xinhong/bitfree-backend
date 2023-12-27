package com.jihai.bitfree.service.strategy;

import com.jihai.bitfree.enums.TaskStatusEnum;
import lombok.Data;


@Data
public class BaseTaskBoardParam {

    private Long userId;

    private Integer taskId;


    public BaseTaskBoardParam() {

    }

    public BaseTaskBoardParam(Long userId, Integer taskId) {
        this.userId = userId;
        this.taskId = taskId;
    }
}
