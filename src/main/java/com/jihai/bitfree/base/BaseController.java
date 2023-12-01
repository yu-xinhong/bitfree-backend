package com.jihai.bitfree.base;

import cn.hutool.core.util.ObjUtil;
import com.jihai.bitfree.base.enums.ReturnCodeEnum;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.dao.ConfigDAO;
import com.jihai.bitfree.dto.resp.UserResp;
import com.jihai.bitfree.entity.ConfigDO;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


@Slf4j
public class BaseController {

    @Autowired
    public HttpServletRequest httpServletRequest;

    @Autowired
    private UserService userService;

    protected UserResp getCurrentUser() {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (ObjUtil.isNull(cookies)){
            throw new BusinessException(ReturnCodeEnum.DO_NOT_INJECT);
        }
        String token = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                token = cookie.getValue();
                break;
            }
        }
        if (! StringUtils.hasText(token)) {
            throw new BusinessException("token is empty");
        }
        return userService.getByToken(token);

        // FIXME 先直接写死
//        return DO2DTOConvert.convertUser(userService.getUser(1L));
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

    protected <T> Result<T> convertFailResult(T data, String message, ReturnCodeEnum returnCodeEnum) {
        Result<T> result = new Result<T>();
        result.setCode(returnCodeEnum.getCode());
        result.setData(data);
        result.setMessage(message);
        return result;
    }


    @Autowired
    private ConfigDAO configDAO;

    protected void checkSecret(String secret) {
        ConfigDO config = configDAO.getByKey(Constants.SECRET);
        if (config == null) {
            throw new RuntimeException("请检查密钥");
        }
        if (! config.getValue().equals(secret)) {
            log.warn("secret is error ! {}", secret);
            throw new RuntimeException("禁止调用");
        }
    }
}
