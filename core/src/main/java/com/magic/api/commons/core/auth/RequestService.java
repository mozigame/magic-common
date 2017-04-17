package com.magic.api.commons.core.auth;

import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限处理Service
 */
public interface RequestService {

    /**
     * 对本次请求进行权限验证
     * @param request           HttpServletRequest
     * @param response          HttpServletResponse
     * @param handlerMethod     HandlerMethod
     * @return 是否验证通过
     */
    boolean request(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod);
}
