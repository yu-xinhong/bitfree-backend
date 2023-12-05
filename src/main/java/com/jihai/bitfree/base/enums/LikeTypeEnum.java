package com.jihai.bitfree.base.enums;

public enum LikeTypeEnum {
    POST(1, "帖子"),
    REPLY(2, "回复");

    private final Integer type;

    private final String desc;

    LikeTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

}
