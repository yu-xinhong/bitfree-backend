package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;

import javax.validation.constraints.NotNull;

public class DeletePostReq extends BaseReq {

    private static final long serialVersionUID = 6951151346706410102L;

    @NotNull(message = "postId不能为空")
    private Long postId;

    private String secret;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
