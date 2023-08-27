package com.jihai.bitfree.base;

import java.io.Serializable;

public class BaseReq implements Serializable {

    private static final long serialVersionUID = 1560110948595155330L;

    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
