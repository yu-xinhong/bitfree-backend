package com.jihai.bitfree.aspect;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jihai.bitfree.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

/**
 * 防重复提交切面
 */
@Aspect
@Component
@Order
@Slf4j
public class RepeatSubmitAspect {

    @Autowired
    private HttpServletRequest httpServletRequest;

    private Cache<String, Object> submitCache = CacheBuilder.newBuilder().maximumSize(200).expireAfterWrite(1, TimeUnit.MINUTES).build();

    @Around("execution(* com.jihai.bitfree.controller..*.*(..))")
    public Object process(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        if (args.length < 1) {
            return proceedingJoinPoint.proceed();
        }

        for (Object parameter : args) {
            Field field = ReflectionUtils.findField(parameter.getClass(), "uuid");
            if (field == null) {
                break;
            }
            field.setAccessible(true);
            String uuid = (String) ReflectionUtils.getField(field, parameter);
            if (uuid == null) {
                break;
            }

            synchronized (this) {
                if (submitCache.getIfPresent(uuid) != null) {
                    throw new BusinessException("重复提交");
                }
                submitCache.put(uuid, new Object());
            }
        }
        return proceedingJoinPoint.proceed();
    }


}
