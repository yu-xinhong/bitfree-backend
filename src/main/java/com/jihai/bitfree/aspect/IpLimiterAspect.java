package com.jihai.bitfree.aspect;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@Order(5)
@Aspect
@RestController
public class IpLimiterAspect extends BaseController {

    // 默认2秒产生一个令牌，ip+方法级别2秒最多一个请求, 提供rest修改能力
    private volatile double DEFAULT_LIMITER_COUNT_PER_SECOND = 0.5;

    Cache<String, RateLimiter> limiterCache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private ConfigService configService;


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

    public void modifyCount(double count, String secret) {
        checkSecret(secret);
        DEFAULT_LIMITER_COUNT_PER_SECOND = count;
        limiterCache.asMap().values().forEach(limiter -> limiter.setRate(count));
    }
}
