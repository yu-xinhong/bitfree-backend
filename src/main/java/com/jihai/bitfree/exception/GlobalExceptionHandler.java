package com.jihai.bitfree.exception;


import cn.hutool.core.collection.CollUtil;
import com.jihai.bitfree.ability.MonitorAbility;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.base.enums.ReturnCodeEnum;
import com.jihai.bitfree.utils.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private MonitorAbility monitorAbility;
    @Autowired
    private RequestUtils requestUtils;

    @ExceptionHandler(BusinessException.class)
    public Result<Object> businessException(BusinessException businessException) {
        String ip = requestUtils.getCurrentIp();
        log.warn("BUSINESS ERROR", businessException);
        monitorAbility.sendMsg(String.format(ip + " business error message: %s  stack : %s",
                businessException.getMessage(), ExceptionUtils.getStackTrace(businessException)));
        return new Result<>(businessException.getReturnCodeEnum());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> methodArgumentNotValid(MethodArgumentNotValidException e) {
        String ip = requestUtils.getCurrentIp();
        log.error("参数校验异常打印： ", e);
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> allErrors = bindingResult.getFieldErrors();
        String message = null;
        if (CollUtil.isNotEmpty(allErrors)) {
            FieldError objectError = allErrors.get(0);
            message = "[" + objectError.getField() + "]" + objectError.getDefaultMessage();
        }
        monitorAbility.sendMsg(String.format(ip + " methodArgumentNotValid error message: %s  stack : %s",
                message, ExceptionUtils.getStackTrace(e)));
        return new Result<>(ReturnCodeEnum.SYSTEM_ERROR, message);
    }

    @ExceptionHandler(Throwable.class)
    public Result<Object> throwAble(Throwable e) {
        String ip = requestUtils.getCurrentIp();
        log.error("Request ERROR", e);
        monitorAbility.sendMsg(String.format(ip + " system error message: %s  stack : %s", e.getMessage(), ExceptionUtils.getStackTrace(e)));
        return new Result<>(ReturnCodeEnum.SYSTEM_ERROR);
    }
}