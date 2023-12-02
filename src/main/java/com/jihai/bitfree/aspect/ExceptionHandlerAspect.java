package com.jihai.bitfree.aspect;


import com.jihai.bitfree.ability.MonitorAbility;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.base.enums.ReturnCodeEnum;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.utils.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Order(1)
@Aspect
public class ExceptionHandlerAspect {

    @Autowired
    private MonitorAbility monitorAbility;

    @Autowired
    private RequestUtils requestUtils;

    @Around("execution(* com.jihai.bitfree.controller..*.*(..))")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) {
        String ip = requestUtils.getCurrentIp();

        try {
            return proceedingJoinPoint.proceed();
        } catch (BusinessException businessException) {
            log.warn("BUSINESS ERROR", businessException);
            monitorAbility.sendMsg(String.format(ip + " business error message: %s  stack : %s", businessException.getMessage(), ExceptionUtils.getStackTrace(businessException)));
            return errorResult(businessException.getReturnCodeEnum() == null ? ReturnCodeEnum.BUSINESS_ERROR : businessException.getReturnCodeEnum(), businessException);
        } catch (Throwable e) {
            log.error("Request ERROR", e);
            monitorAbility.sendMsg(String.format(ip + " system error message: %s  stack : %s", e.getMessage(), ExceptionUtils.getStackTrace(e)));
            return errorResult(ReturnCodeEnum.SYSTEM_ERROR, ReturnCodeEnum.SYSTEM_ERROR.getDesc());
        }
    }

    private Object errorResult(ReturnCodeEnum returnCodeEnum, String desc) {
        Result<Boolean> result = new Result<>();
        result.setCode(returnCodeEnum.getCode());
        result.setMessage(returnCodeEnum.getDesc());
        return result;
    }

    private Object errorResult(ReturnCodeEnum returnCodeEnum, Throwable throwable) {
        Result<Boolean> result = new Result<>();
        result.setCode(returnCodeEnum.getCode());
        result.setMessage(throwable.getMessage());
        return result;
    }
}
