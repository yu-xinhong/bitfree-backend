package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseResp;

import java.util.Date;

public class PostDetailResp extends BaseResp {

    private static final long serialVersionUID = -6737144697361321893L;

    private String avatar;

    private String title;

    private String content;

    private Integer viewCount;

    private Integer likeCount;

    private String creatorName;

    private Long creatorId;

    private Date createTime;

    private Date updateTime;

    private Boolean likePost;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getLikePost() {
        return likePost;
    }

    public void setLikePost(Boolean likePost) {
        this.likePost = likePost;
    }
}
