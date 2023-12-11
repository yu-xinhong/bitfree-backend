package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.aspect.SensitiveText;
import com.jihai.bitfree.base.BaseReq;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class SaveUserReq extends BaseReq {

    private static final long serialVersionUID = -3194199142275222567L;

    @NotNull(message = "头像不能为空")
    private String avatar;

    @SensitiveText
    @NotBlank(message = "昵称不能为空")
    @Length(min = 1, max = 10, message = "昵称长度不在限制内")
    private String name;

    @SensitiveText
    @NotNull(message = "城市不能为空")
    private String city;

    @SensitiveText
    @NotNull(message = "职位不能为空")
    private String position;

    @NotNull(message = "工龄不能为空")
    private Integer seniority;

    private String github;

    private Long inviteUserId;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getSeniority() {
        return seniority;
    }

    public void setSeniority(Integer seniority) {
        this.seniority = seniority;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public Long getInviteUserId() {
        return inviteUserId;
    }

    public void setInviteUserId(Long inviteUserId) {
        this.inviteUserId = inviteUserId;
    }
}
