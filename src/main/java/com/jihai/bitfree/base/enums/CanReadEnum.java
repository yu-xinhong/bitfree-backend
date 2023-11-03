package com.jihai.bitfree.base.enums;

public enum CanReadEnum {

    NO(0, "不能手动读"), YES(1, "可以手动读");

    private Integer value;

    private String desc;

    CanReadEnum(Integer value, String desc) {
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
