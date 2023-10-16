package com.jihai.bitfree.entity;

import com.jihai.bitfree.base.BaseDO;

public class NotificationDO extends BaseDO {

    private String title;

    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
