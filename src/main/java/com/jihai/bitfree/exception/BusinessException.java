package com.jihai.bitfree.exception;

import com.jihai.bitfree.base.enums.ReturnCodeEnum;

public class BusinessException extends RuntimeException {

    private ReturnCodeEnum returnCodeEnum;

    public BusinessException() {
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, ReturnCodeEnum returnCodeEnum) {
        super(message);
        this.returnCodeEnum = returnCodeEnum;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ReturnCodeEnum getReturnCodeEnum() {
        return returnCodeEnum;
    }
}
