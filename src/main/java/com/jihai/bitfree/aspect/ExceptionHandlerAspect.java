package com.jihai.bitfree.aspect;


import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.base.enums.ReturnCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Order(1)
@Aspect
public class ExceptionHandlerAspect {

    @Around("execution(* com.jihai.bitfree.controller..*.*(..))")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) {
        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            Result<Boolean> result = new Result<>();
            result.setCode(ReturnCodeEnum.SYSTEM_ERROR.getCode());
            result.setMessage(e.getMessage());
            return result;
        }
    }
}
