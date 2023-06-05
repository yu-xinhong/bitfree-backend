package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;

import javax.validation.constraints.NotNull;

public class CheckNameReq extends BaseReq {

    private static final long serialVersionUID = -5981796984614022807L;

    @NotNull(message = "哈哈！名字被占用，换一个吧")
    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
