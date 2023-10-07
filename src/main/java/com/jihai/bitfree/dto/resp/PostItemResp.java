package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseResp;

import java.util.Date;

public class PostItemResp extends BaseResp {

    private Long id;

    private String avatar;

    private String title;

    private String creatorName;

    private String updateUserName;

    private Date updateTime;

    private Integer replyCount;

    private Date createTime;

    private Boolean newPost;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getNewPost() {
        return newPost;
    }

    public void setNewPost(Boolean newPost) {
        this.newPost = newPost;
    }
}
