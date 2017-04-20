package com.magic.api.service;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.auth.Access;
import com.magic.api.commons.core.auth.AuthService;
import com.magic.api.commons.core.auth.Order;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.core.exception.CommonException;
import com.magic.api.commons.core.exception.ExceptionFactor;
import com.magic.api.commons.core.log.RequestLogRecord;
import com.magic.api.commons.core.tools.HeaderUtil;
import com.magic.api.commons.core.tools.MauthUtil;
import com.magic.tongtu.service.AuthoriseService;
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
    public Integer auth(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        String authHeader = HeaderUtil.getMauth(request);
        MauthUtil.AuthModel authModel = MauthUtil.getUid(authHeader);
        RequestContext requestContext = RequestContext.getRequestContext();
        RequestLogRecord requestLogRecord = requestContext.getRequestLogRecord();
        requestLogRecord.setAuth(Access.AccessType.RESOURCE.getName());
        int uid = authModel.getUid();
        String resourceId = HeaderUtil.getHeaderResourceId(request);
        String resourceUrl = HeaderUtil.getHeaderResourceUrl(request);
        //权限验证
        verifyAccess(uid, resourceId, resourceUrl);
        return uid;
    }

    /**
     * 权限验证
     *
     * @param uid
     * @param resourceId
     * @param resourceUrl
     */
    private void verifyAccess(int uid, String resourceId, String resourceUrl) {
        if (StringUtils.isEmpty(resourceId)){
            throw ExceptionFactor.SOURCE_ID_HEADER_IS_EMPTY_EXCEPTION;
        }
        if (StringUtils.isEmpty(resourceUrl)){
            throw ExceptionFactor.SOURCE_ID_HEADER_IS_EMPTY_EXCEPTION;
        }
        boolean hasRight = false;
        try {
            hasRight = authoriseService.cherUserPerm(uid, Long.parseLong(resourceId), resourceUrl);
        }catch (CommonException e){
            throw e;
        } catch (Exception e){
            ApiLogger.error("invoke right access dubbo error.", e);
        }
        if (!hasRight){
            throw ExceptionFactor.USER_NO_RESOURCE_ACCESS_EXCEPTION;
        }
    }
}
