package com.jihai.bitfree.entity;

import com.jihai.bitfree.base.BaseDO;

public class MessageNoticeDO extends BaseDO {

    private Long messageId;

    private Long userId;

    private Integer status;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
