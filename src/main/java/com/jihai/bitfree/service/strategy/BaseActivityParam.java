package com.jihai.bitfree.service.strategy;

public class BaseActivityParam {

    private Long userId;

    private Long activityId;

    public BaseActivityParam(Long userId, Long activityId) {
        this.userId = userId;
        this.activityId = activityId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
}
