package com.magic.api.commons.core.auth;

import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.core.log.RequestLogRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 公开暴露接口认证
 */
@Component
@Order(90)
public class PublicAuthService implements AuthService {

    @Override
    public boolean supports(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        Access access = handlerMethod.getMethod().getAnnotation(Access.class);
        return null != access && Access.AccessType.PUBLIC == access.type();

    }

    @Override
    public Integer auth(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        RequestLogRecord requestLogRecord = RequestContext.getRequestContext().getRequestLogRecord();
        requestLogRecord.setUserType(RequestLogRecord.UserType.guest);
        requestLogRecord.setAuth(Access.AccessType.PUBLIC.getName());
        return 1;
    }
}
