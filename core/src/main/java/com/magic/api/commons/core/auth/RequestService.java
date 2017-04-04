package com.magic.api.commons.core.auth;

import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zz
 */
public interface RequestService {

    boolean request(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod);
}
