package com.jihai.bitfree.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum TaskStatusEnum {
    /**
     * 0 待办
     */
    TODO(0,"todo"),
    /**
     * 1 进行中
     */
    DOING(1,"doing"),
    /**
     * 2 结束
     */
    DONE(2,"done"),
    /**
     * 取消操作, 实际为待办状态
     */
    CANCEL(0, "todo");

    private final Integer status;
    private final String desc;
}
