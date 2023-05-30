package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;
import jakarta.validation.constraints.NotNull;

public class LoginReq extends BaseReq {

    @NotNull(message = "邮箱不能为空")
    private String email;

    @NotNull(message = "密码不能为空")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
