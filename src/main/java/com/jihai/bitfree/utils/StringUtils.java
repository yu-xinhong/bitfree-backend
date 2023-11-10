package com.jihai.bitfree.utils;

public class StringUtils {
    public static String compressContent(String content, int compress2Len) {
        if (! org.springframework.util.StringUtils.hasText(content)) {
            return "";
        }
        return content.length() > compress2Len ? content.substring(0, compress2Len) + "..." : content;
    }
}
