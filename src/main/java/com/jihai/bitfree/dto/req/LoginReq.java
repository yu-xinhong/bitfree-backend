package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class LoginReq extends BaseReq {

    @NotNull(message = "邮箱不能为空")
    @Length(max = 100, message = "禁止恶意注入")
    // FIXME test_123@163.com这种账号无法通过
    @Email(regexp = "^[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*@([a-zA-Z0-9]+[-.])+(com|cn|edu|gov|net|org|vip|educ|ru)$",message = "邮箱格式不正确")
    private String email;

    @NotNull(message = "密码不能为空")
    @Length(max = 100, message = "禁止恶意注入")
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
