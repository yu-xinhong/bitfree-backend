package com.jihai.bitfree.base.enums;

public enum OperateTypeEnum {
    INIT_USER(1, "初始化"),
    UPDATE_PASSWORD(2, "修改密码"),
    CHAT(3, "聊天室"),
    LOGIN(4, "登录"),
    CHANGE_IP(5, "切换IP"),
    SHORT_LINK(6, "短链跳转"),
    RESET_PASSWORD(7, "重置密码"),
    LIVE_COINS(8, "在线获取硬币"),
    TASK_COINS(9, "完成开发任务获取激励硬币"),

    INVITE_COINS(10, "邀请获取硬币"),
    INVITED_COINS(11, "被邀请获取硬币"),
    ACTIVITY(12, "活动")
    ;

    OperateTypeEnum(Integer code, String desc) {
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
