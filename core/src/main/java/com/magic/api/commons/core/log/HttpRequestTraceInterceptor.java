package com.magic.api.commons.core.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.core.context.Client;
import com.magic.api.commons.core.context.ClientVersion;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.tools.HeaderUtil;
import com.magic.api.commons.tools.IPUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求日志处理
 *
 * @author zz
 */
public class HttpRequestTraceInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        RequestContext requestContext = RequestContext.getRequestContext();
        Client client = requestContext.getClient();
        requestContext.setRequest(request);
        requestContext.setResponse(response);
        requestContext.setOrigin(HeaderUtil.getOrigin(request));
        RequestLogRecord requestLogRecord = requestContext.getRequestLogRecord();
        requestLogRecord.setStartTime(System.currentTimeMillis());
        requestLogRecord.setRequestId(HeaderUtil.getRequestId(request));
        requestLogRecord.setApi(request.getRequestURI());
        requestLogRecord.setMethod(request.getMethod());
        requestLogRecord.setReferer(HeaderUtil.getReferer(request));
        int appId = HeaderUtil.getAppId(request);
        requestLogRecord.setAppid(appId);
        client.setAppId(appId);
        String clientType = HeaderUtil.getClientType(request);
        requestLogRecord.setPlatform(clientType);
        client.setClientType(Client.ClientType.get(clientType));
        requestLogRecord.setParameters(request.getParameterMap());
        String ip = IPUtil.getReqIp(request);
        requestLogRecord.setOriginalIp(ip);
        String ips[] = ip.split(",");
        requestContext.setIp(ips[0]);
        requestLogRecord.setRequestIp(request.getRemoteAddr());
        ClientVersion clientVersion = new ClientVersion(HeaderUtil.getClientVersion(request));
        requestLogRecord.setClientVersion(clientVersion);
        client.setClientVersion(clientVersion);
        String clientNdeviceId = HeaderUtil.getClientNdeviceId(request);
        requestLogRecord.setNdeviceid(clientNdeviceId);
        client.setDeviceId(clientNdeviceId);
        requestLogRecord.setUserAgent(HeaderUtil.getUserAgent(request));
        //token
        String mauth = HeaderUtil.getMauth(request);
        requestContext.setToken(mauth);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        RequestLogRecord requestLogRecord = RequestContext.getRequestContext().getRequestLogRecord();
        requestLogRecord.setPage(null != modelAndView);
        if (null != modelAndView) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("view", modelAndView.getViewName());
            jsonObject.put("model", modelAndView.getModel());
            requestLogRecord.setResponse(JSON.toJSONString(jsonObject));
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestLogRecord requestLogRecord = RequestContext.getRequestContext().getRequestLogRecord();
        ApiLogger.requset(requestLogRecord.toStringShort());
        super.afterCompletion(request, response, handler, ex);
        RequestContext.clearRequestContext();
    }

}