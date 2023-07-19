package com.jihai.bitfree.entity;

import com.jihai.bitfree.base.BaseDO;

public class UserLikeDO extends BaseDO {

    private Long userId;

    private Long targetId;

    private Integer type;

    private Boolean value;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
}
