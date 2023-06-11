package com.jihai.bitfree.aspect;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jihai.bitfree.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

@Aspect
//@Component
@Slf4j
@Order(5)
public class SensitiveCheckAspect {

    private Set<String> sensitiveSet = Sets.newHashSet();

    {
        try {

            List<String> fileNameList = Lists.newArrayList("政治类.txt", "网址.txt", "色情类.txt");

            for (String file : fileNameList) {
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sensitiveWord/" + file);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sensitiveSet.add(line);
                }
            }
            log.info("sensitive word {}", sensitiveSet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Around("execution(* com.jihai.bitfree.controller..*.*(..))")
    public Object process(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        if (args.length > 0) {
            for (Object param : args) {
                Field[] fields = param.getClass().getDeclaredFields();
                if (fields.length > 0) {
                    for (Field field : fields) {
                        if (field.getAnnotation(SensitiveText.class) != null) {
                            field.setAccessible(true);
                            Object checkContent = ReflectionUtils.getField(field, param);
                            if (! StringUtils.hasText(checkContent.toString())) continue;

                            for (String word : sensitiveSet) {
                                if (checkContent.toString().contains(word)) {
                                    log.warn("someone input sensitive {} word {} ", checkContent, word);
                                    throw new BusinessException("请检查敏感词");
                                }
                            }
                        }
                    }
                }
            }
        }
        return proceedingJoinPoint.proceed();
    }
}
