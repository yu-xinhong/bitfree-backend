package com.jihai.bitfree.base.enums;

public enum PostTypeEnum {

    POST(0, "post"),
    VIDEO(1, "video");

    private Integer type;

    private String desc;


    PostTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }
}
