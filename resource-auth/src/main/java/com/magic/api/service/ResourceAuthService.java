package com.magic.api.service;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.auth.Access;
import com.magic.api.commons.core.auth.AuthService;
import com.magic.api.commons.core.auth.Order;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.core.log.RequestLogRecord;
import com.magic.api.commons.tools.HeaderUtil;
import com.magic.api.commons.tools.MauthUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ResourceAuthService
 * 资源权限访问校验
 *
 * @author zj
 * @date 2017/4/20
 */
@Component
@Order(5)
public class ResourceAuthService implements AuthService{
    @Override
    public boolean supports(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        Access access = handlerMethod.getMethod().getAnnotation(Access.class);
        if (null == access || null == access.type() || Access.AccessType.RESOURCE != access.type()) {
            return false;
        }
        String mauth = HeaderUtil.getMauth(request);
        return MauthUtil.canAuth(mauth);
    }

    @Override
    public Integer auth(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        String authHeader = HeaderUtil.getMauth(request);
        MauthUtil.AuthModel authModel = MauthUtil.getUid(authHeader);
        RequestContext requestContext = RequestContext.getRequestContext();
        RequestLogRecord requestLogRecord = requestContext.getRequestLogRecord();
        requestLogRecord.setAuth(Access.AccessType.RESOURCE.getName());
        int uid = authModel.getUid();
        ApiLogger.info("resource auth uid:" + uid);
        return uid;
    }
}
