package com.jihai.bitfree.base.enums;

public enum PostTypeEnum {

    POST(0, "post"),
    VIDEO(1, "video");

    private final Integer type;

    private final String desc;


    PostTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getType() {
        return type;
    }
}
