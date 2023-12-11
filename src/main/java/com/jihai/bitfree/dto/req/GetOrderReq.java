package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;

public class GetOrderReq extends BaseReq {

    private Long activityId;

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
}
