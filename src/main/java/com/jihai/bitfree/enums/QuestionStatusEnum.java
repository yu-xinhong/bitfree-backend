package com.jihai.bitfree.enums;

import com.jihai.bitfree.exception.BusinessException;

public enum QuestionStatusEnum {

    VERIFYING(0, "审核中"),

    VERIFIED(1, "审核过"),

    REJECT(2, "拒绝");

    private final Integer status;

    private final String desc;

    QuestionStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static QuestionStatusEnum getByCode(Integer status) {
        if (status == null) return null;
        for (QuestionStatusEnum questionStatusEnum : QuestionStatusEnum.values()) {
            if (questionStatusEnum.getStatus().equals(status)) return questionStatusEnum;
        }
        throw new BusinessException("未知题目状态");
    }


    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
