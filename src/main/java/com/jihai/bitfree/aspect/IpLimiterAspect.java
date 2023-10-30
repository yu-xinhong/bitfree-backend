package com.jihai.bitfree.aspect;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.service.ConfigService;
import com.jihai.bitfree.utils.RequestUtils;
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

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@Order(5)
@Aspect
@RestController
public class IpLimiterAspect {

    // 默认2秒产生一个令牌，ip+方法级别2秒最多一个请求, 提供rest修改能力
    private volatile double DEFAULT_LIMITER_COUNT_PER_SECOND = 1;

    Cache<String, RateLimiter> limiterCache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();

    @Autowired
    private RequestUtils requestUtils;

    @Autowired
    private ConfigService configService;

    private List<String> WHITE_LIST = Lists.newArrayList("com.jihai.bitfree.controller.MessageController.getRecentList", "com.jihai.bitfree.controller.UserController.getDetail");


    @Around("execution(* com.jihai.bitfree.controller..*.*(..))")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String ip = requestUtils.getCurrentIp();

        Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        String methodName = proceedingJoinPoint.getTarget().getClass().getName() + "." + methodSignature.getName();

        if (WHITE_LIST.contains(methodName)) {
            return proceedingJoinPoint.proceed();
        }

        String recordKey = ip + "->" + methodName;
        // 此处不需要考虑并发get，底层已lock创建
        RateLimiter rateLimiter = limiterCache.get(recordKey, () -> RateLimiter.create(DEFAULT_LIMITER_COUNT_PER_SECOND));

        if (! rateLimiter.tryAcquire()) {
            log.warn("ip -> method {} 单位请求次数过多", recordKey);
            throw new BusinessException("操作太快");
        }
        return proceedingJoinPoint.proceed();
    }

    public void modifyCount(double count) {
        DEFAULT_LIMITER_COUNT_PER_SECOND = count;
        limiterCache.asMap().values().forEach(limiter -> limiter.setRate(count));
    }
}
