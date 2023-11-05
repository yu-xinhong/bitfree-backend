package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class ResetAccountReq extends BaseReq {

    @NotNull
    @Email(message = "请输入有效的邮箱格式")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
