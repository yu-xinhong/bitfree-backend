package com.jihai.bitfree.base.enums;

public enum ReturnCodeEnum {
    SUCCESS(200, "成功"),
    SYSTEM_ERROR(500, "系统异常"),
    BUSINESS_ERROR(501, "业务异常"),
    SECRET_ERROR(502, "密钥错误"),
    USER_OLD_PASSWORD_ERROR(403, "原密码错误"),
    SAME_PASSWORD_ERROR(405, "不能与原密码一样"),
    SEND_MAIL_ERROR(406, "调用邮件服务器错误");

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
