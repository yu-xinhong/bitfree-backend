package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;

import javax.validation.constraints.NotNull;

public class ConfigReq extends BaseReq {

    private static final long serialVersionUID = 7300571886429519730L;

    @NotNull(message = "key不能为空")
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
