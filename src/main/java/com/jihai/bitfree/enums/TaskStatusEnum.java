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
     * 3 提交, 提交后需review才能到{@link #DONE}状态
     */
    SUBMIT(3, "submit")

    ;

    private final Integer status;
    private final String desc;
}
