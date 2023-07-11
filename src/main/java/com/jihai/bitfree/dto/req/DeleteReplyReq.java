package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;

import javax.validation.constraints.NotNull;

public class DeleteReplyReq extends BaseReq  {

    private static final long serialVersionUID = -8369083239437853478L;

    @NotNull(message = "id不能为空")
    private Long id;

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
