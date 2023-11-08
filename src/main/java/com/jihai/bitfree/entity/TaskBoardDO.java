package com.jihai.bitfree.entity;

import com.jihai.bitfree.base.BaseDO;

public class TaskBoardDO extends BaseDO {

    private String content;

    private Integer coins;

    private Long userId;

    private Integer status;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
