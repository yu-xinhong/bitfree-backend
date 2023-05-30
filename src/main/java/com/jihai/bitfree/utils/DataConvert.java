package com.jihai.bitfree.utils;


import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
public class DataConvert {
    public static List<Long> convertValue2List(String value) {
        List<Long> resultList = Lists.newArrayList();
        try {
            if (ObjectUtils.isEmpty(value) || ! StringUtils.hasText(value)) {
                return resultList;
            }
            for (String split : value.split(",")) {
                resultList.add(Long.valueOf(split));
            }
            return resultList;
        } catch (NumberFormatException e) {
            log.error("convert to list error, config value is {}", value, e);
        }
        return resultList;
    }
}
