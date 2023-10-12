package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseResp;

import java.util.Date;

public class CollectResp extends BaseResp {

    private Long postId;

    private String title;

    private String creatorName;

    private Date createTime;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
