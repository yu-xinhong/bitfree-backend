package com.jihai.bitfree.base.enums;

public enum TaskBoardStatusEnum {

    TODO(0, "待办事项"),

    DOING(1, "在办事项"),

    DONE(2, "已办事项");

    private final Integer status;

    private final String desc;

    TaskBoardStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
