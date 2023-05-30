package com.jihai.bitfree.base;

import com.jihai.bitfree.base.enums.ReturnCodeEnum;
import com.jihai.bitfree.dto.resp.UserDTO;

public class BaseController {

    protected UserDTO getCurrentUser() {
        return null;
    }

    protected <T> Result<T> convertSuccessResult(T data) {
        Result<T> result = new Result<T>();
        result.setCode(ReturnCodeEnum.SUCCESS.getCode());
        result.setData(data);
        return result;
    }

    protected <T> Result<T> convertFailResult(T data, String message) {
        Result<T> result = new Result<T>();
        result.setCode(ReturnCodeEnum.SYSTEM_ERROR.getCode());
        result.setData(data);
        result.setMessage(message);
        return result;
    }


}
