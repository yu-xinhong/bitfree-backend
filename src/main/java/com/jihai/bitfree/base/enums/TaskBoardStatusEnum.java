package com.jihai.bitfree.base.enums;

public enum TaskBoardStatusEnum {

    TODO(0, "待办事项"),

    DOING(1, "在办事项"),

    DONE(2, "已办事项");

    private Integer status;

    private String desc;

    TaskBoardStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
