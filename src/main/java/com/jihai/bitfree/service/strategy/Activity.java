package com.jihai.bitfree.service.strategy;

public interface Activity<P extends BaseActivityParam> {

    boolean support(ActivityTypeEnum activityTypeEnum);

    boolean kill(P param);
}
