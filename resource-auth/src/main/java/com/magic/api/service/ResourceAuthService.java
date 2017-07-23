package com.magic.api.service;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.auth.Access;
import com.magic.api.commons.core.auth.AuthService;
import com.magic.api.commons.core.auth.Order;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.core.exception.CommonException;
import com.magic.api.commons.core.exception.ExceptionFactor;
import com.magic.api.commons.core.log.RequestLogRecord;
import com.magic.api.commons.core.tools.CookieUtil;
import com.magic.api.commons.core.tools.HeaderUtil;
import com.magic.api.commons.core.tools.MauthUtil;
import com.magic.owner.service.AuthoriseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.annotation.Resource;
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

    @Resource
    AuthoriseService authoriseService;

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
    public Long auth(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        String authHeader = HeaderUtil.getMauth(request);
        MauthUtil.AuthModel authModel = MauthUtil.getUid(authHeader);
        String newToken = authModel.getNewToken();
        if(StringUtils.isNoneEmpty(newToken)) {
            CookieUtil.setMauth(response, newToken);
        }
        RequestContext requestContext = RequestContext.getRequestContext();
        String deviceId = HeaderUtil.getDeviceId(request);
        requestContext.getClient().setDeviceId(deviceId);
        RequestLogRecord requestLogRecord = requestContext.getRequestLogRecord();
        requestLogRecord.setAuth(Access.AccessType.RESOURCE.getName());
        long uid = authModel.getUid();
        String resourceUrl = request.getRequestURI();
        //权限验证
        verifyAccess(uid, resourceUrl);
        return uid;
    }

    /**
     * 权限验证
     *
     * @param uid
     * @param resourceUrl
     */
    private void verifyAccess(long uid, String resourceUrl) {

        boolean hasRight;
        try {
            hasRight = authoriseService.cherUserPerm(uid, resourceUrl);
        }catch (CommonException e){
            throw e;
        } catch (Exception e){
            ApiLogger.error(String.format("invoke right access dubbo error. msg: %s", e.getMessage()));
            hasRight = true;
        }
        if (!hasRight){
            throw ExceptionFactor.USER_NO_RESOURCE_ACCESS_EXCEPTION;
        }
    }
}
