package com.jihai.bitfree.entity;

import com.jihai.bitfree.base.BaseDO;

public class ReplyDO extends BaseDO {

    private static final long serialVersionUID = -6716178323427064193L;

    private Long sendUserId;

    private Long ReceiverId;

    private Long postId;

    private Long subReplyId;

    private String replyContent;

    public Long getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(Long sendUserId) {
        this.sendUserId = sendUserId;
    }

    public Long getReceiverId() {
        return ReceiverId;
    }

    public void setReceiverId(Long receiverId) {
        ReceiverId = receiverId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getSubReplyId() {
        return subReplyId;
    }

    public void setSubReplyId(Long subReplyId) {
        this.subReplyId = subReplyId;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }
}
