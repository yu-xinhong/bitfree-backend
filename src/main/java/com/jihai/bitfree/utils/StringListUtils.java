package com.jihai.bitfree.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

public class StringListUtils {

    public static List<String> str2List(String str) {
        if (StringUtils.isEmpty(str)) return Lists.newArrayList();
        String[] split = str.split(",");
        return Arrays.asList(split);
    }

    public static String list2Str(List<String> list) {
        if (CollectionUtils.isEmpty(list)) return "";
        return Joiner.on(",").join(list);
    }
}
