package com.jihai.bitfree.dto.req;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class AdoptReplyReq implements Serializable {

    private static final long serialVersionUID = 9020247868100611340L;

    @NotNull(message = "帖子id不能为空")
    private Long postId;

    @NotNull(message = "评论id不能为空")
    private Long replyId;

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setReplyId(Long replyId) {
        this.replyId = replyId;
    }

    public Long getReplyId() {
        return replyId;
    }
}
