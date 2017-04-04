package com.magic.api.commons.core.auth;

import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.core.log.RequestLogRecord;
import com.magic.api.commons.exception.CommonException;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.tools.HeaderUtil;
import com.magic.api.commons.tools.MauthUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 内部访问方式认证
 */
@Component
@Order(100)
public class InternalAuthService implements AuthService {

    @Override
    public boolean supports(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        RequestContext requestContext = RequestContext.getRequestContext();
        String ip = requestContext.getIp();
        if (StringUtils.isBlank(ip)) {
            ApiLogger.error("获取IP失败");
            return false;
        }
        String mauth = HeaderUtil.getMauth(request);
        if (MauthUtil.canAuth(mauth)) {
            return false;
        }
        //TODO 考虑动态验证码 核实身份
        return null != ip && (ip.startsWith("10.") || ip.startsWith("127.")) || ip.startsWith("172.");
    }

    @Override
    public Integer auth(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        int uid = NumberUtils.toInt(HeaderUtil.getzbUid(request));
        if (0 >= uid) {
            throw new CommonException("Matrix uid header is empty.");
        }
        RequestLogRecord requestLogRecord = RequestContext.getRequestContext().getRequestLogRecord();
        requestLogRecord.setUserType(RequestLogRecord.UserType.internal);
        requestLogRecord.setAuth(Access.AccessType.INTERNAL.getName());
        return uid;
    }
}
