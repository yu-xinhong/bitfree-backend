package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ReplyReq extends BaseReq {

    private static final long serialVersionUID = 4761989008848044725L;

    @NotNull(message = "postId为空")
    @Min(0)
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
