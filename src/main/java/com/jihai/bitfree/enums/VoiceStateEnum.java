package com.jihai.bitfree.enums;

public enum VoiceStateEnum {

    CLOSE(0, "关闭"),
    OPEN(1, "开启");

    private final Integer value;

    private final String desc;

    VoiceStateEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
