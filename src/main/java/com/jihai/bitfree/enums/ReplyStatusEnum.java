package com.jihai.bitfree.enums;

public enum ReplyStatusEnum {
    REWARD(0, "采纳");

    private final Integer status;

    private final String desc;

    ReplyStatusEnum(Integer status, String desc) {
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
