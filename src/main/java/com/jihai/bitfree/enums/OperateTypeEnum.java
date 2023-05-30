package com.jihai.bitfree.enums;

public enum OperateTypeEnum {

    UPDATE_PASSWORD(0, "修改密码");

    private Integer value;

    private String description;

    OperateTypeEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    public Integer getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
