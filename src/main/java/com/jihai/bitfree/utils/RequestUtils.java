package com.jihai.bitfree.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class RequestUtils {

    @Autowired
    private HttpServletRequest httpServletRequest;

    public String getCurrentIp() {
        return httpServletRequest.getHeader("X-Real-IP");
    }
}
