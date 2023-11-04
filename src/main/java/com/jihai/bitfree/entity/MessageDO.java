package com.jihai.bitfree.entity;

import com.jihai.bitfree.base.BaseDO;

public class MessageDO extends BaseDO {

    private Long sendUserId;

    private Long targetMessageId;

    private String content;

    public Long getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(Long sendUserId) {
        this.sendUserId = sendUserId;
    }

    public Long getTargetMessageId() {
        return targetMessageId;
    }

    public void setTargetMessageId(Long targetMessageId) {
        this.targetMessageId = targetMessageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
