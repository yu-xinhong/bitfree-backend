package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;

import javax.validation.constraints.NotNull;

public class ResetPasswordReq extends BaseReq {

    private static final long serialVersionUID = 5482188868605110432L;

    @NotNull
    private Long id;

    @NotNull
    private String secret;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
