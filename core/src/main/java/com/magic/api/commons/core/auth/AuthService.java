package com.magic.api.commons.core.auth;

import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthService {

    boolean supports(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod);

    Integer auth(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod);
}
