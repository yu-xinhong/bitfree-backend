package com.jihai.bitfree.unit;

import com.jihai.bitfree.entity.UserDO;

public class BaseUnit {

    public static void main(String[] args) {
        UserDO userDO = new UserDO();
        userDO.setCity("杭州");
        System.out.println(userDO);
    }
}
