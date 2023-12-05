package com.jihai.bitfree.base.enums;

public enum UserLevelEnum {

    COMMUNITY(1, "社区版"),

    ULTIMATE(2, "旗舰版");

    private final Integer level;

    private final String desc;

    UserLevelEnum(Integer level, String desc) {
        this.level = level;
        this.desc = desc;
    }

    public Integer getLevel() {
        return level;
    }

    public String getDesc() {
        return desc;
    }

}
