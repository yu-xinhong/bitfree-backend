package com.jihai.bitfree.base.enums;

public enum OperateTypeEnum {
    INIT_USER(1, "初始化"),
    UPDATE_PASSWORD(2, "修改密码");

    OperateTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Integer code;

    private String desc;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
