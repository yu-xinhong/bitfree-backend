package com.jihai.bitfree.aspect;


import com.google.common.collect.Sets;
import com.jihai.bitfree.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Set;

@Aspect
@Component
@Slf4j
@Order(5)
public class SensitiveCheckAspect {

    private Set<String> sensitiveSet = Sets.newHashSet();

    {
        ClassPathResource classPathResource = new ClassPathResource("sensitiveWord");
        try {
            File dir = classPathResource.getFile();
            File[] files = dir.listFiles();
            for (File file : files) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.replace(" ", "").length() > 0) {
                        sensitiveSet.add(line.replace(",", ""));
                    }
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
