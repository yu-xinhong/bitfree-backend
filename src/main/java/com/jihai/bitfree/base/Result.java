package com.jihai.bitfree.base;

import com.jihai.bitfree.base.enums.ReturnCodeEnum;

import java.io.Serializable;

public class Result<T> implements Serializable {

    private static final long serialVersionUID = 7163242669595049214L;

    private T data;

    private Integer code;

    private String message;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Result() {
    }

    public Result(ReturnCodeEnum returnCodeEnum) {
        this.data = null;
        this.code = returnCodeEnum.getCode();
        this.message = returnCodeEnum.getDesc();
    }
    public Result(ReturnCodeEnum returnCodeEnum, String message) {
        this.data = null;
        this.code = returnCodeEnum.getCode();
        this.message = message;
    }
}
