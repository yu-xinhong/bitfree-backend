package com.jihai.bitfree.service.strategy;

public class BaseActivityParam {

    private Long userId;

    private Long activityId;

    private Integer type;

    public BaseActivityParam(Long userId, Long activityId, Integer type) {
        this.userId = userId;
        this.activityId = activityId;
        this.type = type;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
