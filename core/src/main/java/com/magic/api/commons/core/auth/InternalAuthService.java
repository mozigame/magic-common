package com.magic.api.commons.core.auth;

import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.core.exception.ExceptionFactor;
import com.magic.api.commons.core.log.RequestLogRecord;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.tools.HeaderUtil;
import com.magic.api.commons.core.tools.MauthUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 内部访问方式认证
 * * @see cn.zb.commons.framework.tools.HeaderUtil#getIp  保证该方法能正确获取代理后用户真实IP
 */
@Component
@Order(100)
public class InternalAuthService implements AuthService {

    /**
     * 本地访问
     */
    public static final String LOCALHOST = "localhost";

    /**
     * 本地访问
     */
    public static final String LOCALHOST_IP = "127.";

    /**
     * A类私有地址
     */
    public static final String PRIVATE_IP_A = "10.";

    /**
     * B类私有地址正则
     */
    public static final String PRIVATE_IP_B_REGEX = "^172[.](1[0-6]|2[0-9]|3[0-1])[.](25[0-5]|[0-9]{1,2})[.](25[0-5]|[0-9]{1,2})$";

    /**
     * C类私有地址
     */
    public static final String PRIVATE_IP_C = "192.168.";

    /**
     * 当前验证方式 是否支持本次请求
     * @param request           HttpServletRequest
     * @param response          HttpServletResponse
     * @param handlerMethod     HandlerMethod
     * @return  是否支持
     */
    @Override
    public boolean supports(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        RequestContext requestContext = RequestContext.getRequestContext();
        String ip = requestContext.getIp();
        if (StringUtils.isBlank(ip)) {
            ApiLogger.error("miss ip");
            return false;
        }
        String mauth = HeaderUtil.getMauth(request);
        if (MauthUtil.canAuth(mauth)) {
            return false;
        }
        //考虑动态验证码核实身份 提供接口扩展
        return null != ip && (ip.startsWith(LOCALHOST) || ip.startsWith(LOCALHOST_IP)) || ip.startsWith(PRIVATE_IP_A)
                || ip.matches(PRIVATE_IP_B_REGEX) || ip.toLowerCase().equals(PRIVATE_IP_C);
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
        int uid = NumberUtils.toInt(HeaderUtil.getzbUid(request));
        if (0 >= uid) {
            throw ExceptionFactor.MATRIX_UID_HEADER_IS_EMPTY_EXCEPTION;
        }
        RequestLogRecord requestLogRecord = RequestContext.getRequestContext().getRequestLogRecord();
        requestLogRecord.setAuth(Access.AccessType.INTERNAL.getName());
        return uid;
    }
}
