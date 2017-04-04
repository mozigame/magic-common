package com.magic.api.commons.core.context;

import com.magic.api.commons.core.log.RequestLogRecord;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zz
 */
public class RequestContext {

    private RequestContext(){}

    private static final ThreadLocal<RequestContext> zbContextThreadLocal = new ThreadLocal<RequestContext>();

    /**
     * 是否读取主库
     */
    private boolean readMaster = false;

    /**
     * 返回JSON数据
     */
    private Result result = new Result();

    /**
     * 用户ID APP端服务使用
     */
    private int uid;

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

    public static RequestContext getRequestContext() {
        RequestContext requestContext = zbContextThreadLocal.get();
        if (null == requestContext) {
            requestContext = new RequestContext();
            zbContextThreadLocal.set(requestContext);
        }
        return requestContext;
    }

    /**
     * 非异常情况下给客户端提示错误信息
     * 用户逻辑上的错误 非代码异常情况下
     * @param errorMsg  客户端展示错误信息
     */
    public static void setErrorMsg(String errorMsg) {
        Result result = getRequestContext().getResult();
        result.setApistatus(0);
        result.setErrorMsg(errorMsg);
    }

    public static void clearRequestContext() {
        zbContextThreadLocal.remove();
    }

    public class Result {

        /**
         * JSON数据包裹
         */
        private boolean wrap = true;

        /**
         * 框架级别apistatus状态值
         */
        private int apistatus = 1;

        /**
         * 返回客户端错误提示
         */
        private String errorMsg;

        public boolean isWrap() {
            return wrap;
        }

        public void setWrap(boolean wrap) {
            this.wrap = wrap;
        }

        public int getApistatus() {
            return apistatus;
        }

        public void setApistatus(int apistatus) {
            this.apistatus = apistatus;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }
    }

    public static ThreadLocal<RequestContext> getzbContextThreadLocal() {
        return zbContextThreadLocal;
    }

    public boolean isReadMaster() {
        return readMaster;
    }

    public void setReadMaster(boolean readMaster) {
        this.readMaster = readMaster;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getIp() {
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
}
