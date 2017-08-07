package com.magic.api.commons.core.log;

import com.alibaba.fastjson.JSON;
import com.magic.api.commons.core.context.ClientVersion;
import com.magic.api.commons.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * 请求日志记录
 * @author zz
 */
public class RequestLogRecord {

    /**
     * 数据分割
     */
    private static final String SPLIT = "\t";

    private static final String DEFAULT_FIELD = "-";

    /**
     * 请求定位ID
     */
    private String requestId;//TODO 整合dubbo httpclient

    /**
     * 认证方式
     */
    private String auth;

    /**
     * 如果由Http发起记录URL 如果由Dubbo发起记录具体方法
     */
    private String api;

    /**
     * 是否为页面
     */
    private boolean page;

    /**
     * 请求方式GET POST等
     */
    private String method;

    /**
     * 从哪个页面跳转
     */
    private String referer;

    /**
     * HttpResponseStatus
     */
    private int responseStatus;//TODO 异常处理捕获500code

    /**
     * responseSize
     */
    private int responseSize;//TODO 获取

    /**
     * kaishi
     */
    private long startTime;

    /**
     * 接口响应使用时间
     */
    private long useTime;

    /**
     * 来源 appid
     */
    private int appid;

    /**
     * Android IOS
     */
    private String platform;

    /**
     * 用户ID
     */
    private long uid;//TODO 登录页面时 两种用户都无法获取

    /**
     * 用户类型
     */
    private UserType userType;

    /**
     * 客户端请求参数
     */
    private Map<String, String[]> parameters;

    /**
     * 用户ip,如果是内网服务器端调用，该ip是调用方通过 Api-RemoteIP机制传递的用户ip //TODO httpclient header传递 RPCContext传递
     */
    private String originalIp;

    /**
     * 本次请求发起机器IP
     */
    private String requestIp;

    /**
     * 客户端版本
     */
    private ClientVersion clientVersion;

    /**
     * 设备ID
     */
    private String ndeviceid;

    /**
     * User-Agent
     */
    private String userAgent;

    /**
     * 返回给用户的数据
     */
    private Object response;

    /**
     * 其他数据 方便数据分析
     */
    private Map<String, Object> extend;

    /**
     * 用户类型
     */
    public enum UserType {

        unknown(0, "unknown"), magic(1, "zb"), guest(2, "guest"), web(3, "web"), internal(4, "internal");

        UserType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        /**
         * 1.平台用户 2.非注册用户
         */
        private int value;
        private String name;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public boolean isPage() {
        return page;
    }

    public void setPage(boolean page) {
        this.page = page;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public void setResponseSize(int responseSize) {
        this.responseSize = responseSize;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getUseTime() {
        return useTime;
    }

    public void setUseTime(long useTime) {
        this.useTime = useTime;
    }

    public int getAppid() {
        return appid;
    }

    public void setAppid(int appid) {
        this.appid = appid;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Map<String, String[]> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String[]> parameters) {
        this.parameters = parameters;
    }

    public String getOriginalIp() {
        return originalIp;
    }

    public void setOriginalIp(String originalIp) {
        this.originalIp = originalIp;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public ClientVersion getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(ClientVersion clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getNdeviceid() {
        return ndeviceid;
    }

    public void setNdeviceid(String ndeviceid) {
        this.ndeviceid = ndeviceid;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Map<String, Object> getExtend() {
        if (null == extend) {
            extend = new HashMap<>();
        }
        return extend;
    }

    public void setExtend(Map<String, Object> extend) {
        this.extend = extend;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        appendField(stringBuilder, requestId);
        appendField(stringBuilder, auth);
        appendField(stringBuilder, api);
        appendField(stringBuilder, page);
        appendField(stringBuilder, method);
        appendField(stringBuilder, referer);
        appendField(stringBuilder, responseStatus);
        appendField(stringBuilder, responseSize);
        appendField(stringBuilder, appid);
        appendField(stringBuilder, platform);
        appendField(stringBuilder, uid);
        appendField(stringBuilder, null != userType ? userType.getName() : DEFAULT_FIELD);
        appendField(stringBuilder, parameters);
        appendField(stringBuilder, originalIp);
        appendField(stringBuilder, requestIp);
        appendField(stringBuilder, null != clientVersion ? clientVersion.toString() : DEFAULT_FIELD);
        appendField(stringBuilder, ndeviceid);
        appendField(stringBuilder, userAgent);
        appendField(stringBuilder, response);
        appendField(stringBuilder, extend);
        useTime = System.currentTimeMillis() - startTime;
        stringBuilder.append(useTime);
        return String.valueOf(stringBuilder);
    }
    
    public String toStringShort() {
    	String resp=String.valueOf(response);
    	if(resp!=null && (resp.indexOf("{\"apistatus\":1")>=0)
    			&& resp.length()>100){
    		resp=resp.substring(0,  100);
    	}
        StringBuilder stringBuilder = new StringBuilder();
        appendField(stringBuilder, requestId);
        appendField(stringBuilder, auth);
        appendField(stringBuilder, api);
        appendField(stringBuilder, page);
        appendField(stringBuilder, method);
        appendField(stringBuilder, referer);
        appendField(stringBuilder, responseStatus);
        appendField(stringBuilder, responseSize);
        appendField(stringBuilder, appid);
        appendField(stringBuilder, platform);
        appendField(stringBuilder, uid);
        appendField(stringBuilder, null != userType ? userType.getName() : DEFAULT_FIELD);
        appendField(stringBuilder, parameters);
        appendField(stringBuilder, originalIp);
        appendField(stringBuilder, requestIp);
        appendField(stringBuilder, null != clientVersion ? clientVersion.toString() : DEFAULT_FIELD);
        appendField(stringBuilder, ndeviceid);
        appendField(stringBuilder, userAgent);
        appendField(stringBuilder, resp);
        appendField(stringBuilder, extend);
        useTime = System.currentTimeMillis() - startTime;
        stringBuilder.append(useTime);
        return String.valueOf(stringBuilder);
    }

    /**
     * 组装数据
     * @param stringBuilder StringBuilder
     * @param field 字段数据
     */
    private void appendField(StringBuilder stringBuilder, String field) {
        stringBuilder.append(StringUtils.isEmpty(field) ? DEFAULT_FIELD : field).append(SPLIT);
    }

    /**
     * 组装数据
     * @param stringBuilder StringBuilder
     * @param field 字段数据
     */
    private void appendField(StringBuilder stringBuilder, boolean field) {
        stringBuilder.append(field).append(SPLIT);
    }

    /**
     * 组装数据
     * @param stringBuilder StringBuilder
     * @param field 字段数据
     */
    private void appendField(StringBuilder stringBuilder, int field) {
        stringBuilder.append(field).append(SPLIT);
    }

    /**
     * 组装数据
     * @param stringBuilder StringBuilder
     * @param field 字段数据
     */
    private void appendField(StringBuilder stringBuilder, long field) {
        stringBuilder.append(field).append(SPLIT);
    }

    /**
     * 组装数据
     * @param stringBuilder StringBuilder
     * @param field 字段数据
     */
    private void appendField(StringBuilder stringBuilder, Object field) {
        stringBuilder.append(null == field ? DEFAULT_FIELD : JSON.toJSONString(field)).append(SPLIT);
    }
}
