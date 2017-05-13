package com.magic.api.commons.core.context;

import com.magic.api.commons.core.log.RequestLogRecord;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zz
 */
public class RequestContext {

    private RequestContext(){}

    private static final ThreadLocal<RequestContext> contextThreadLocal = new ThreadLocal<RequestContext>();

    /**
     * 返回JSON数据
     */
    private Result result = new Result();

    /**
     * 是否读取Master
     */
    private boolean readMaster;

    /**
     * 用户ID APP端服务使用
     */
    private long uid;

    /**
     * 用户token
     */
    private String token;

    /**
     * 客户端相关数据
     */
    private Client client = new Client();

    /**
     * 客户端IP
     */
    private String ip;

    /**
     * 请求日志记录
     */
    private RequestLogRecord requestLogRecord = new RequestLogRecord();

    /**
     * HttpServletRequest
     */
    private HttpServletRequest request;

    /**
     * HttpServletResponse
     */
    private HttpServletResponse response;

    /**
     * 扩展字段
     */
    private Map<String, Object> ext = new HashMap<String, Object>();

    public static RequestContext getRequestContext() {
        RequestContext requestContext = contextThreadLocal.get();
        if (null == requestContext) {
            requestContext = new RequestContext();
            contextThreadLocal.set(requestContext);
        }
        return requestContext;
    }

    public static void clearRequestContext() {
        contextThreadLocal.remove();
    }

    public class Result {

        /**
         * JSON数据包裹
         */
        private boolean wrap = true;


        public boolean isWrap() {
            return wrap;
        }

        public void setWrap(boolean wrap) {
            this.wrap = wrap;
        }

    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public boolean isReadMaster() {
        return readMaster;
    }

    public void setReadMaster(boolean readMaster) {
        this.readMaster = readMaster;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getIp() {
        if (null == ip) {
            return "";
        }
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public RequestLogRecord getRequestLogRecord() {
        return requestLogRecord;
    }

    public void setRequestLogRecord(RequestLogRecord requestLogRecord) {
        this.requestLogRecord = requestLogRecord;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public Map<String, Object> getExt() {
        return ext;
    }

    public String getToken() {
        if (token == null){
            return "";
        }
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
