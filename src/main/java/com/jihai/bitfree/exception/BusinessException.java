package com.jihai.bitfree.exception;

import com.jihai.bitfree.base.enums.ReturnCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private ReturnCodeEnum returnCodeEnum;

    public BusinessException(String message){
        super(message);
    }

    public BusinessException(ReturnCodeEnum returnCodeEnum) {
        super(returnCodeEnum.getDesc());
        this.returnCodeEnum = returnCodeEnum;
    }
    public BusinessException(String message, ReturnCodeEnum returnCodeEnum) {
        super(message);
        this.returnCodeEnum = returnCodeEnum;
    }
}
