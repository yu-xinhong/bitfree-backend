package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;
import jakarta.validation.constraints.NotNull;

public class AddUserReq extends BaseReq {

    private static final long serialVersionUID = -6255319867205574466L;

    @NotNull(message = "email is empty")
    private String email;

    private String secret;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
