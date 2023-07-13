package com.jihai.bitfree.aspect;


import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.base.enums.ReturnCodeEnum;
import com.jihai.bitfree.exception.BusinessException;
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
        } catch (BusinessException businessException) {
            log.warn("BUSINESS ERROR", businessException);
            return errorResult(ReturnCodeEnum.BUSINESS_ERROR, businessException);
        } catch (Throwable e) {
            log.error("Request ERROR", e);
            return errorResult(ReturnCodeEnum.SYSTEM_ERROR, ReturnCodeEnum.SYSTEM_ERROR.getDesc());
        }
    }

    private Object errorResult(ReturnCodeEnum returnCodeEnum, String desc) {
        Result<Boolean> result = new Result<>();
        result.setCode(returnCodeEnum.getCode());
        result.setMessage(desc);
        return result;
    }

    private Object errorResult(ReturnCodeEnum returnCodeEnum, Throwable throwable) {
        Result<Boolean> result = new Result<>();
        result.setCode(returnCodeEnum.getCode());
        result.setMessage(throwable.getMessage());
        return result;
    }
}
