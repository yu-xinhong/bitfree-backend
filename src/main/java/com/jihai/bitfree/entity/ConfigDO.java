package com.jihai.bitfree.entity;

import com.jihai.bitfree.base.BaseDO;

public class ConfigDO extends BaseDO {

    private static final long serialVersionUID = -4454005001431872171L;

    private String key;

    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
