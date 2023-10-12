package com.jihai.bitfree.entity;

import com.jihai.bitfree.base.BaseDO;

public class CollectDO extends BaseDO {

    private Long userId;

    private Long targetId;

    private Integer type;

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

}
