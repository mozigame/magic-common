package com.magic.api.commons.core.auth;

import com.magic.api.commons.core.context.RequestContext;
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

@Component
public class RequestServiceImpl implements RequestService, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private List<AuthService> authServices;

    @Override
    public boolean request(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        return doAuth(request, response, handlerMethod);
    }

    private boolean doAuth(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        AuthService authService = getAuth(request, response, handlerMethod);
        Integer uid = authService.auth(request, response, handlerMethod);
        if (null != uid && 0 < uid) {
            RequestContext requestContext = RequestContext.getRequestContext();
            requestContext.setUid(uid);
            requestContext.getRequestLogRecord().setUid(uid);
            return true;
        } else {
            throw new CommonException("UID认证失败");
        }
    }

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
            throw new CommonException("无法验证");
        }
        return auth;
    }

    private void LogError(HttpServletRequest request, HandlerMethod handlerMethod) {
        try {
            RequestContext requestContext = RequestContext.getRequestContext();
            String ip = requestContext.getIp();
            String authHeader = HeaderUtil.getMauth(request);
            Method method = handlerMethod.getMethod();
            Access annotation = method.getAnnotation(Access.class);
            ApiLogger.error("无法验证 method " + method.toString() + " annotation " + annotation + " ip " + ip + " mauth " + authHeader);
        } catch (Exception e) {
            ApiLogger.error("LogError", e);
        }
    }

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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        initAuthServices();
    }
}
