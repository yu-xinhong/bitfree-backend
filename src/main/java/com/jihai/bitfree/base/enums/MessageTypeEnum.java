package com.jihai.bitfree.base.enums;

public enum MessageTypeEnum {

    MESSAGE(0, "聊天消息"),
    NOTIFICATION(1, "站内通知"),
    MESSAGE_MENTION_UNREAD(2, "聊天消息被@");

    private Integer type;

    private String desc;

    MessageTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
