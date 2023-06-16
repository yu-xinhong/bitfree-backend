package com.jihai.bitfree.aspect;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import com.jihai.bitfree.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@Order(5)
@Aspect
public class IpLimiterAspect {

    private static final double DEFAULT_LIMITER_COUNT_PER_SECOND = 1;

    Cache<String, RateLimiter> limiterCache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Around("execution(* com.jihai.bitfree.controller..*.*(..))")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String ip = httpServletRequest.getHeader("X-Real-IP");

        Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        String methodName = proceedingJoinPoint.getTarget().getClass().getName() + "." + methodSignature.getName();
        String recordKey = ip + "->" + methodName;

        RateLimiter rateLimiter = limiterCache.get(recordKey, () -> RateLimiter.create(DEFAULT_LIMITER_COUNT_PER_SECOND));

        if (! rateLimiter.tryAcquire()) {
            log.error("ip -> method {} 单位请求次数过多", recordKey);
            throw new BusinessException("触发限流, 再请求封禁");
        }
        return proceedingJoinPoint.proceed();
    }
}
