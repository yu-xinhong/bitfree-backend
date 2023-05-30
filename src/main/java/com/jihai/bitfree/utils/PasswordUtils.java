package com.jihai.bitfree.utils;

import java.util.UUID;

public class PasswordUtils {
    public static String generatePwd() {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}
