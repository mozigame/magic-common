package com.magic.api.commons.core.auth;

import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.core.log.RequestLogRecord;
import com.magic.api.commons.tools.HeaderUtil;
import com.magic.api.commons.tools.MauthUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 客户端MAuth认证方式
 */
@Component
@Order(10)
public class MAuthService implements AuthService {

    @Override
    public boolean supports(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        Access access = handlerMethod.getMethod().getAnnotation(Access.class);
        if (null == access || null == access.type() || Access.AccessType.COMMON != access.type()) {
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
        int uid = authModel.getUid();
        int appId = authModel.getAppId();
        requestContext.getClient().setAppId(appId);
        requestLogRecord.setUserType(RequestLogRecord.UserType.zb);
        requestLogRecord.setAuth(Access.AccessType.COMMON.getName());
        requestLogRecord.setAppid(appId);
        return uid;
    }

}
