package com.jihai.bitfree.entity;

import com.jihai.bitfree.base.BaseDO;

public class MessageDO extends BaseDO {

    private Long sendUserId;

    private String content;

    public Long getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(Long sendUserId) {
        this.sendUserId = sendUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
