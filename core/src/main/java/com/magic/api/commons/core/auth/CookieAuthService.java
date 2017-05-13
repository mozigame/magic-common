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
 * H5或PHP Cookie认证方式
 * @author zj
 */
@Component
@Order(30)
public class CookieAuthService implements AuthService {

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
        if (null == access || null == access.type() || Access.AccessType.COOKIE != access.type()) {
            return false;
        }
        String mauth = HeaderUtil.getMauth(request);
        return MauthUtil.canAuth(mauth);
    }

    /**
     * 解析验证本次请求
     * @param request           HttpServletRequest
     * @param response          HttpServletResponse
     * @param handlerMethod     HandlerMethod
     * @return  用户ID
     */
    @Override
    public Long auth(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        String authHeader = HeaderUtil.getMauth(request);
        String deviceId = HeaderUtil.getDeviceId(request);
        MauthUtil.AuthModel authModel = MauthUtil.getUid(authHeader, deviceId);
        String newToken = authModel.getNewToken();
        if(StringUtils.isNoneEmpty(newToken)) {
            CookieUtil.setMauth(response, newToken);
        }
        RequestContext requestContext = RequestContext.getRequestContext();
        RequestLogRecord requestLogRecord = requestContext.getRequestLogRecord();
        long uid = authModel.getUid();
        requestContext.getClient().setDeviceId(deviceId);
        requestLogRecord.setAuth(Access.AccessType.COOKIE.getName());
        return uid;
    }

}
