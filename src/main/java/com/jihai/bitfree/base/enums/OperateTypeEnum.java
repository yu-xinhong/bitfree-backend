package com.jihai.bitfree.base.enums;

public enum OperateTypeEnum {
    INIT_USER(1, "初始化"),
    UPDATE_PASSWORD(2, "修改密码"),
    CHAT(3, "聊天室"),
    LOGIN(4, "登录"),
    CHANGE_IP(5, "切换IP"),
    SHORT_LINK(6, "短链跳转"),
    RESET_PASSWORD(7, "重置密码");

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
