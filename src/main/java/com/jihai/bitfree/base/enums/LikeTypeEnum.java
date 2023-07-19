package com.jihai.bitfree.base.enums;

public enum LikeTypeEnum {
    POST(1, "帖子"),
    REPLY(2, "回复");

    private Integer type;

    private String desc;

    LikeTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
