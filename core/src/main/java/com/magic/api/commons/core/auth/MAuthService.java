package com.magic.api.commons.core.auth;

import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.core.log.RequestLogRecord;
import com.magic.api.commons.core.tools.CookieUtil;
import com.magic.api.commons.core.tools.HeaderUtil;
import com.magic.api.commons.core.tools.MauthUtil;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * 当前验证方式 是否支持本次请求
     * @param request           HttpServletRequest
     * @param response          HttpServletResponse
     * @param handlerMethod     HandlerMethod
     * @return  是否支持
     */
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
    public Long auth(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        String authHeader = HeaderUtil.getMauth(request);
        MauthUtil.AuthModel authModel = MauthUtil.getUid(authHeader);
        String newToken = authModel.getNewToken();
        if(StringUtils.isNoneEmpty(newToken)) {
            CookieUtil.setMauth(response, newToken);
        }
        RequestContext requestContext = RequestContext.getRequestContext();
        RequestLogRecord requestLogRecord = requestContext.getRequestLogRecord();
        long uid = authModel.getUid();
        requestLogRecord.setAuth(Access.AccessType.COMMON.getName());
        return uid;
    }

}
