package com.jihai.bitfree.entity;

import com.jihai.bitfree.base.BaseDO;

public class NotificationDO extends BaseDO {

    private String title;

    private String content;

    private String userList;

    private Integer canRead;

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

    public String getUserList() {
        return userList;
    }

    public void setUserList(String userList) {
        this.userList = userList;
    }

    public Integer getCanRead() {
        return canRead;
    }

    public void setCanRead(Integer canRead) {
        this.canRead = canRead;
    }
}
