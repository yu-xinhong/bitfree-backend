package com.jihai.bitfree.service.strategy;

public abstract class Activity<P extends BaseActivityParam> {

    protected abstract boolean support(ActivityTypeEnum activityTypeEnum);

    public boolean kill(P param) {
        if (! support(ActivityTypeEnum.getByCode(param.getType()))) return true;
        return doKill(param);
    }

    protected abstract boolean doKill(P param);
}
