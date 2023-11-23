package com.jihai.bitfree.config;

import com.alibaba.fastjson.JSON;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.base.enums.ReturnCodeEnum;
import com.jihai.bitfree.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.NestedServletException;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * XSS和SQL注入攻击过滤器
 * @author : Immortal Chengge
 * @Description : //TODO
 **/
@Component
@WebFilter(urlPatterns = "/*", asyncSupported = true)
@Order(1)
@Slf4j
public class XssAndSqlFilter implements Filter {

    @Autowired
    private SecurityConfig securityConfig;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //跳过不需要的Xss校验的地址
        // 不启用或者已忽略的URL不拦截
        if (!securityConfig.getXss().isEnable() || isExcludeUrl(req.getServletPath())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        //注入xss过滤器实例
        XssAndSqlHttpServletRequestWrapper reqW = new XssAndSqlHttpServletRequestWrapper(req);
        try {
            //过滤
            filterChain.doFilter(reqW, response);
        } catch (Exception e) {
            log.error("XSS过滤时捕获异常,转通用错误响应");
            Result<Boolean> result = new Result<>();
            //  Spring内部会在FrameworkServlet#processRequest处封装为嵌套异常, 需要以内部实际异常为准
            if (e instanceof NestedServletException && e.getCause() instanceof  BusinessException) {
                result.setCode(((BusinessException) e.getCause()).getReturnCodeEnum().getCode());
                result.setMessage(((BusinessException) e.getCause()).getReturnCodeEnum().getDesc());
            } else {
                result.setCode(ReturnCodeEnum.SYSTEM_ERROR.getCode());
                result.setMessage(e.getMessage());
            }
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(result));
        }
    }


    /**
     * 判断是否为忽略的URL
     *
     * @param url URL路径
     * @return true-忽略，false-过滤
     */
    private boolean isExcludeUrl(String url) {
        if (securityConfig.getXss().getExcludePaths() == null || securityConfig.getXss().getExcludePaths().isEmpty()) {
            return false;
        }
        return securityConfig.getXss().getExcludePaths().stream().map(
                pattern -> Pattern
                        .compile("^" + pattern))
                        .map(p -> p.matcher(url))
                        .anyMatch(Matcher::find);
    }
}
