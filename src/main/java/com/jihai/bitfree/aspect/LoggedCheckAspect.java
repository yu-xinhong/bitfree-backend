package com.jihai.bitfree.aspect;

import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Slf4j
@Component
@Profile({"local", "prod"})
@Order(2)
public class LoggedCheckAspect {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private UserDAO userDAO;

    @Around("@annotation(com.jihai.bitfree.aspect.LoggedCheck)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Cookie[] cookies = httpServletRequest.getCookies();
        String ip = httpServletRequest.getHeader("X-Real-IP");
        if (cookies == null) {
            log.error("The request {} headers not exists cookies", ip);
            throw new BusinessException(Constants.NOT_LOGIN);
        }
        String token = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Constants.TOKEN)) {
                token = cookie.getValue();
                break;
            }
        }
        if (token == null) {
            log.error("The request {} try to access protected resources", ip);
            throw new BusinessException(Constants.NOT_LOGIN);
        }
        if (userDAO.getByToken(token) == null) {
            log.error("The request {} try fake token", ip);
            throw new BusinessException(Constants.NOT_LOGIN);
        }
        return proceedingJoinPoint.proceed();
    }
}
