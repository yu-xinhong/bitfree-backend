package com.jihai.bitfree.utils;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.util.UUID;

public class PasswordUtils {
    public static String generatePwd() {
        return UUID.randomUUID().toString().substring(0, 6);
    }

    public static String md5(String password) {
        try {
            byte[] bytes = password.getBytes("UTF-8");
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(bytes);
            return DatatypeConverter.printHexBinary(md5.digest());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
