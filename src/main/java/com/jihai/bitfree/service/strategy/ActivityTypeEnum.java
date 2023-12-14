package com.jihai.bitfree.service.strategy;

import com.jihai.bitfree.exception.BusinessException;

public enum ActivityTypeEnum {

    MEETING(1, "会议");

    private final Integer code;

    private final String desc;

    ActivityTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ActivityTypeEnum getByCode(Integer code) {
        if (code == null) throw new BusinessException("code不能为空");
        for (ActivityTypeEnum activityTypeEnum : ActivityTypeEnum.values()) {
            if (activityTypeEnum.getCode().equals(code)) return activityTypeEnum;
        }
        throw new BusinessException("找不到对应的类型");
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
