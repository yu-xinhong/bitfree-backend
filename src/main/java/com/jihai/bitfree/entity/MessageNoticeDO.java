package com.jihai.bitfree.entity;

import com.jihai.bitfree.base.BaseDO;

public class MessageNoticeDO extends BaseDO {

    private Long messageId;

    /**
     * 被通知人
     */
    private Long userId;

    private Integer type;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
