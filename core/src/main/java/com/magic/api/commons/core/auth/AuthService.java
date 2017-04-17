package com.magic.api.commons.core.auth;

import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 验证Service
 * @author zz
 */
public interface AuthService {

    /**
     * 当前验证方式 是否支持本次请求
     * @param request           HttpServletRequest
     * @param response          HttpServletResponse
     * @param handlerMethod     HandlerMethod
     * @return  是否支持
     */
    boolean supports(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod);

    /**
     * 解析验证本次请求
     * @param request           HttpServletRequest
     * @param response          HttpServletResponse
     * @param handlerMethod     HandlerMethod
     * @return  用户ID
     */
    Integer auth(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod);
}
