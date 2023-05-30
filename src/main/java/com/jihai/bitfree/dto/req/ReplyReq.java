package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReplyReq extends BaseReq {

    private static final long serialVersionUID = 4761989008848044725L;

    @NotNull(message = "postId为空")
    @Size(min = 0, message = "postId不对")
    private Long postId;

    private Long replyId;

    @NotNull(message = "回复内容为空")
    private String replyContent;

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

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }
}
