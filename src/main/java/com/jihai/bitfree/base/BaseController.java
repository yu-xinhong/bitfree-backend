package com.jihai.bitfree.base;

import com.jihai.bitfree.base.enums.ReturnCodeEnum;
import com.jihai.bitfree.dto.resp.UserResp;
import com.jihai.bitfree.service.UserService;
import com.jihai.bitfree.utils.DO2DTOConvert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@CrossOrigin("*")
public class BaseController {

    @Autowired
    public HttpServletRequest httpServletRequest;

    @Autowired
    private UserService userService;

    protected UserResp getCurrentUser() {
        /*Cookie[] cookies = httpServletRequest.getCookies();
        String token = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                token = cookie.getValue();
                break;
            }
        }
        if (! StringUtils.hasText(token)) {
            throw new RuntimeException("token is empty");
        }
        return userService.getByToken(token);*/

        // FIXME 先直接写死
        return DO2DTOConvert.convertUser(userService.getUser(2L));
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
