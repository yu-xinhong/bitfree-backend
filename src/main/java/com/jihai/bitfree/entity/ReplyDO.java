package com.jihai.bitfree.entity;

import com.jihai.bitfree.base.BaseDO;

public class ReplyDO extends BaseDO {

    private static final long serialVersionUID = -6716178323427064193L;

    private Long sendUserId;

    private Long receiverId;

    private Long postId;

    // 如果是回复别人，这个是目标回复id
    private Long targetReplyId;

    private String replyContent;

    public Long getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(Long sendUserId) {
        this.sendUserId = sendUserId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getTargetReplyId() {
        return targetReplyId;
    }

    public void setTargetReplyId(Long targetReplyId) {
        this.targetReplyId = targetReplyId;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }
}
