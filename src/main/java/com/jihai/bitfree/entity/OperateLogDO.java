package com.jihai.bitfree.entity;

import com.jihai.bitfree.base.BaseDO;

public class OperateLogDO extends BaseDO {

    private static final long serialVersionUID = -4138251340319255865L;

    private Long userId;

    private Integer type;

    private String remark;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
