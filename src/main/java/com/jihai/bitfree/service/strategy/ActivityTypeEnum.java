package com.jihai.bitfree.service.strategy;

public enum ActivityTypeEnum {

    MEETING(1, "会议");

    private final Integer code;

    private final String desc;

    ActivityTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
