package com.jihai.bitfree.enums;

public enum PostStatusEnum {
    REWARD(0, "悬赏");

    private final Integer status;

    private final String desc;

    PostStatusEnum(Integer status, String desc) {
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
