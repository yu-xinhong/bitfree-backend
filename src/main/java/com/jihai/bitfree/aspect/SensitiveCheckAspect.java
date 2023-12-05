package com.jihai.bitfree.aspect;


import com.google.common.collect.Sets;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.regex.Pattern;

@Aspect
@Component
@Slf4j
@Order(5)
public class SensitiveCheckAspect {

    private Set<String> sensitiveSet = Sets.newHashSet();


    @Autowired
    private ConfigService configService;

    Pattern pattern = Pattern.compile("(<[a-z]*\\s*/>)|(\\s*<br */?>\\s*)");

    @PostConstruct
    public void initLoadSensitiveWords() {
        String value = configService.getByKey(Constants.SENSITIVE_WORDS);
        if (StringUtils.isEmpty(value)) {
            log.warn("sensitive is empty");
            return ;
        }

        String valueJson = value.replace("\n", "");
        for (String word : valueJson.split(",")) {
            sensitiveSet.add(word.trim());
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
                            if (org.apache.commons.lang3.StringUtils.isBlank(pattern.matcher(checkContent.toString())
                                    .replaceAll(""))) {
                                throw new BusinessException("消息为空标签");
                            }
                        }
                    }
                }
            }
        }
        return proceedingJoinPoint.proceed();
    }
}
