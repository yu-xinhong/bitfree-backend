package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseResp;

import java.util.Date;

public class MessageResp extends BaseResp {

    private Long id;

    private Long userId;

    private String userName;

    private String avatar;

    private String content;

    private Date createTime;

    private Long mentionedUserId;

    private String mentionedUserName;

    private String mentionedContent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getMentionedUserId() {
        return mentionedUserId;
    }

    public void setMentionedUserId(Long mentionedUserId) {
        this.mentionedUserId = mentionedUserId;
    }

    public String getMentionedUserName() {
        return mentionedUserName;
    }

    public void setMentionedUserName(String mentionedUserName) {
        this.mentionedUserName = mentionedUserName;
    }

    public String getMentionedContent() {
        return mentionedContent;
    }

    public void setMentionedContent(String mentionedContent) {
        this.mentionedContent = mentionedContent;
    }
}
