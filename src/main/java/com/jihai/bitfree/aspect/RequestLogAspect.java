package com.jihai.bitfree.aspect;


import com.alibaba.fastjson.JSON;
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

@Aspect
@Component
@Order(0)
@Slf4j
public class RequestLogAspect {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Around("execution(* com.jihai.bitfree.controller..*.*(..))")
    public Object process(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            Signature signature = proceedingJoinPoint.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            Object[] args = proceedingJoinPoint.getArgs();
            String methodName = proceedingJoinPoint.getTarget().getClass().getName() + "." + methodSignature.getName();

            StringBuffer requestLog = new StringBuffer();
            requestLog.append(String.format("remote %s request to %s", httpServletRequest.getRemoteAddr(), methodName));

            if (args.length > 0) {
                StringBuffer paramsBuffer = new StringBuffer();
                for (Object param : args) {
                    paramsBuffer.append(JSON.toJSONString(param)).append(",");
                }
                requestLog.append(", params -> " + paramsBuffer.substring(0, paramsBuffer.length() - 1));
            }


            // 理论上，这是最外层的切面，不可能抛异常
            Object returnObj = proceedingJoinPoint.proceed();

            if (returnObj != null) {
                requestLog.append(", result -> " + JSON.toJSONString(returnObj));
            }
            log.info(requestLog.toString());
            return returnObj;
        } catch (Throwable e) {
            log.error("RequestLogAspect ERROR", e);
            // 兜底
            return proceedingJoinPoint.proceed();
        }
    }
}
