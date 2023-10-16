package com.jihai.bitfree.base.enums;

public enum MessageStatusEnum {
    NOT_READ(0, "notRead"), READ(1, "read");

    private Integer status;

    private String desc;

    MessageStatusEnum(Integer status, String desc) {
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
