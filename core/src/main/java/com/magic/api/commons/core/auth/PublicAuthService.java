package com.magic.api.commons.core.auth;

import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.core.log.RequestLogRecord;
import com.magic.api.commons.tools.HeaderUtil;
import com.magic.api.commons.tools.MauthUtil;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * 游客用户ID
     */
    public static final int GUEST_UID = 1;

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
        return null != access && Access.AccessType.PUBLIC == access.type();

    }

    /**
     * 解析验证本次请求
     * @param request           HttpServletRequest
     * @param response          HttpServletResponse
     * @param handlerMethod     HandlerMethod
     * @return  用户ID
     */
    @Override
    public Integer auth(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        RequestContext requestContext = RequestContext.getRequestContext();
        RequestLogRecord requestLogRecord = requestContext.getRequestLogRecord();
        requestLogRecord.setAuth(Access.AccessType.PUBLIC.getName());
        String authHeader = HeaderUtil.getMauth(request);
        if (StringUtils.isEmpty(authHeader)) {
            return GUEST_UID;
        }
        MauthUtil.AuthModel authModel = MauthUtil.getUid(authHeader);
        return authModel.getUid();
    }
}
