package com.jihai.bitfree.base.enums;

public enum ReturnCodeEnum {
    SUCCESS(200, "成功"),
    SYSTEM_ERROR(500, "系统异常");

    ReturnCodeEnum(Integer code, String desc) {
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
