package com.jihai.bitfree.entity;

import com.jihai.bitfree.base.BaseDO;

import java.util.Date;

public class CheckInDO extends BaseDO {

    private Long userId;

    private Date date;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
