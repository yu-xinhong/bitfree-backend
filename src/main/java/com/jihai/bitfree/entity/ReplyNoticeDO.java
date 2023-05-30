package com.jihai.bitfree.entity;

import com.jihai.bitfree.base.BaseDO;

public class ReplyNoticeDO extends BaseDO {

    private static final long serialVersionUID = 5870937826059543998L;

    private Long postId;

    private Long replyId;

    private Long notifyUserId;

    private Integer status;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getReplyId() {
        return replyId;
    }

    public void setReplyId(Long replyId) {
        this.replyId = replyId;
    }

    public Long getNotifyUserId() {
        return notifyUserId;
    }

    public void setNotifyUserId(Long notifyUserId) {
        this.notifyUserId = notifyUserId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
