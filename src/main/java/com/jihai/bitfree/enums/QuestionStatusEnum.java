package com.jihai.bitfree.enums;

public enum QuestionStatusEnum {

    COMMITTED(0, "提交"),

    VERIFIED(1, "审核过"),

    REJECT(2, "拒绝");

    private final Integer status;

    private final String desc;

    QuestionStatusEnum(Integer status, String desc) {
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
