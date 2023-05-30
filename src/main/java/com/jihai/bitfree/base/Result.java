package com.jihai.bitfree.base;

import java.io.Serializable;

public class Result<T> implements Serializable {

    private static final long serialVersionUID = 7163242669595049214L;

    private T data;

    private Boolean code;

    private String message;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Boolean getCode() {
        return code;
    }

    public void setCode(Boolean code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
