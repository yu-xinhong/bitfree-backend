package com.jihai.bitfree.base.enums;

public enum OperateTypeEnum {
    INIT_USER(1, "初始化", false),
    UPDATE_PASSWORD(2, "修改密码", false),
    CHAT(3, "聊天室", false),
    LOGIN(4, "登录", false),
    CHANGE_IP(5, "切换IP", false),
    SHORT_LINK(6, "短链跳转", false),
    RESET_PASSWORD(7, "重置密码", false),
    LIVE_COINS(8, "在线时长", true),
    TASK_COINS(9, "完成任务", true),

    INVITE_COINS(10, "邀请用户", true),
    INVITED_COINS(11, "被邀请激励", true),
    ACTIVITY(12, "参与活动", true),
    SIGN_IN(13, "每日签到", true),
    POST_CONSUME(14, "发帖消费", true),
    REPLY_BE_LIKED(15, "评论点赞激励", true),
    POST_BE_LIKED(16, "帖子点赞激励", true),
    POST_BE_COLLECTED(17, "帖子收藏激励", true),
    POST_UN_COLLECT(18, "帖子取消收藏", true),
    ;

    OperateTypeEnum(Integer code, String desc, Boolean coinCorrelation) {
        this.code = code;
        this.desc = desc;
        this.coinCorrelation = coinCorrelation;
    }

    private final Integer code;

    private final String desc;
    /**
     * 和硬币是否相关
     */
    private final Boolean coinCorrelation;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public Boolean getCoinCorrelation() {
        return coinCorrelation;
    }
}
