package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;

import javax.validation.constraints.NotNull;

public class ModifyLimitReq extends BaseReq {

    private static final long serialVersionUID = 2346195956977518079L;

    @NotNull(message = "count不能为空")
    private Double count;

    @NotNull(message = "密钥不能为空")
    private String secret;

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
