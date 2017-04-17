package com.magic.api.commons.core.auth;

import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.core.exception.ExceptionFactor;
import com.magic.api.commons.exception.CommonException;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.tools.HeaderUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 权限处理Service
 */
@Component
public class RequestServiceImpl implements RequestService, ApplicationContextAware {

    /**
     * Spring ApplicationContext
     */
    private ApplicationContext applicationContext;

    /**
     * 权限验证列表 按照顺序依次验证
     */
    private List<AuthService> authServices;

    /**
     * 对本次请求进行权限验证
     * @param request           HttpServletRequest
     * @param response          HttpServletResponse
     * @param handlerMethod     HandlerMethod
     * @return 是否验证通过
     */
    @Override
    public boolean request(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        AuthService authService = getAuth(request, response, handlerMethod);
        Integer uid = authService.auth(request, response, handlerMethod);
        if (null != uid && 0 < uid) {
            RequestContext requestContext = RequestContext.getRequestContext();
            requestContext.setUid(uid);
            requestContext.getRequestLogRecord().setUid(uid);
            return true;
        } else {
            throw ExceptionFactor.INVALID_UID_EXCEPTION;
        }
    }

    /**
     * 从验证列表中匹配验证方式
     * @param request       HttpServletRequest
     * @param response      HttpServletResponse
     * @param handlerMethod HandlerMethod
     * @return  验证Service
     */
    private AuthService getAuth(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        AuthService auth = null;
        for (AuthService authService : authServices) {
            if (authService.supports(request, response, handlerMethod)) {
                auth = authService;
                break;
            }
        }
        //不能支持的验证方式
        if (null == auth) {
            LogError(request, handlerMethod);
            throw ExceptionFactor.AUTH_FAILED_EXCEPTION;
        }
        return auth;
    }

    /**
     * 错误日志记录
     * @param request           HttpServletRequest
     * @param handlerMethod     HandlerMethod
     */
    private void LogError(HttpServletRequest request, HandlerMethod handlerMethod) {
        try {
            RequestContext requestContext = RequestContext.getRequestContext();
            String ip = requestContext.getIp();
            String authHeader = HeaderUtil.getMauth(request);
            Method method = handlerMethod.getMethod();
            Access annotation = method.getAnnotation(Access.class);
            ApiLogger.error("con not auth method " + method.toString() + " annotation " + annotation + " ip " + ip + " mauth " + authHeader);
        } catch (Exception e) {
            ApiLogger.error("LogError", e);
        }
    }

    /**
     * 初始化验证Service
     */
    private void initAuthServices() {
        if (null != authServices) {
            return;
        }
        synchronized (RequestService.class) {
            if (null != authServices) {
                return;
            }
            authServices = new ArrayList<AuthService>();
            authServices.addAll(applicationContext.getBeansOfType(AuthService.class).values());
            Collections.sort(authServices, new Comparator<AuthService>() {

                @Override
                public int compare(AuthService o1, AuthService o2) {
                    return getOrderValue(o1).compareTo(getOrderValue(o2));
                }

                private Integer getOrderValue(AuthService authService) {
                    Order order = authService.getClass().getAnnotation(Order.class);
                    if (null == order) {
                        throw new RuntimeException(authService.getClass() + "未添加" + Order.class + "注解");
                    }
                    return order.value();
                }
            });
        }
    }

    /**
     * Spring钩子注入ApplicationContext
     * @param applicationContext    ApplicationContext
     * @throws BeansException   BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        initAuthServices();
    }
}
