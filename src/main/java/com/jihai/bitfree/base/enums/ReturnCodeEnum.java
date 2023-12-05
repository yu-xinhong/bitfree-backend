package com.jihai.bitfree.base.enums;

public enum ReturnCodeEnum {
    SUCCESS(200, "成功"),
    SYSTEM_ERROR(500, "系统异常"),
    BUSINESS_ERROR(501, "业务异常"),
    SECRET_ERROR(502, "密钥错误"),
    USER_OLD_PASSWORD_ERROR(403, "原密码错误"),
    SAME_PASSWORD_ERROR(405, "不能与原密码一样"),
    SEND_MAIL_ERROR(406, "调用邮件服务器错误"),
    MAX_LIMIT(503, "登录失败次数超过上线阈值"),
    LOGIN_ACCOUNT_ERROR(504, "邮箱或密码错误"),
    ILLEGAL_CHARACTERS_ERROR(505, "参数中含有非法字符"),
    DO_NOT_INJECT(4003, "禁止注入扫描"),
    NOT_LOGIN(4003, "请重新登录!"),
    ;
    ReturnCodeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final Integer code;

    private final String desc;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
