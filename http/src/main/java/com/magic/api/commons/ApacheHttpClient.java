package com.magic.api.commons;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.thread.StandardThreadExecutor;
import com.magic.api.commons.utils.StringUtils;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

public class ApacheHttpClient implements ApiHttpClient {

    private static final Logger logger = LogManager.getLogger(ApacheHttpClient.class);
    public static final int DEFAULT_SIZE = 1024 * 1024 * 10;
    public static final int DEFAULT_MAX_CON_PER_HOST = 150;
    public static final int DEFAULT_CONN_TIME_OUT = 2000;
    public static final int DEFAULT_SO_TIME_OUT = 3000;
    public final static String DEFAULT_CHARSET = "utf-8";
    private static final URLCodec urlCodec = new URLCodec("utf-8");
    private static ApacheHttpClient DEFAULT_INSTANCE = new ApacheHttpClient();
    private MultiThreadedHttpConnectionManager connectionManager;
    private HttpClient client;
    private int maxSize;
    private String proxyHostPort;
    private int soTimeOut;
    private ExecutorService httpPool;
    private ApiHttpClient.ExceptionHandler exceptionHandler;
    private ApiHttpClient.AccessLog accessLog = new DefaultHttpClientAceessLog();

    public ApacheHttpClient() {
        this(DEFAULT_MAX_CON_PER_HOST, DEFAULT_CONN_TIME_OUT, DEFAULT_SO_TIME_OUT, DEFAULT_SIZE);
    }

    public ApacheHttpClient(int conTimeOutMs, int soTimeOutMs) {
        this(DEFAULT_MAX_CON_PER_HOST, conTimeOutMs, soTimeOutMs, DEFAULT_SIZE, 1, 300);
    }

    public ApacheHttpClient(int maxConPerHost, int conTimeOutMs, int soTimeOutMs, int maxSize) {
        this(maxConPerHost, conTimeOutMs, soTimeOutMs, maxSize, 1, 300);
    }

    public ApacheHttpClient(int maxConPerHost, int conTimeOutMs, int soTimeOutMs, int maxSize, int minThread, int maxThread) {
        connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = connectionManager.getParams();
        params.setMaxTotalConnections(600);// 这个值要小于tomcat线程池是800
        params.setDefaultMaxConnectionsPerHost(maxConPerHost);
        params.setConnectionTimeout(conTimeOutMs);
        params.setSoTimeout(soTimeOutMs);
        this.soTimeOut = soTimeOutMs;

        HttpClientParams clientParams = new HttpClientParams();
        // 忽略cookie 避免 Cookie rejected 警告
        clientParams.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
        client = new HttpClient(clientParams, connectionManager);
        this.maxSize = maxSize;
        httpPool = new StandardThreadExecutor(minThread, maxThread);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                httpPool.shutdown();
                connectionManager.shutdown();
            }
        }));
        this.exceptionHandler = new ExceptionHandler() {

            @Override
            public void handle(RuntimeException e) {
                if (e instanceof ApiHttpClientExcpetion) {
                    throw e;
                } else {
                    throw new ApiHttpClientExcpetion(e.getMessage(), e);
                }
            }
        };
    }

    public static ApacheHttpClient getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    private static void addHeader(HttpMethod method, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                method.setRequestHeader(entry.getKey(), entry.getValue());
            }
            // 需要设置明确的Host
            if (headers.get("host") != null) {
                method.getParams().setVirtualHost(headers.get("host"));
            }
        }
    }

    private static String urlEncode(String str, String charset) {
        try {
            return urlCodec.encode(str, charset);
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    private String mapToString(Map<String, ?> nameValues) {
        StringBuffer sb = new StringBuffer();
        if (nameValues == null) {
            return sb.toString();
        }
        for (Map.Entry<String, ?> entry : nameValues.entrySet()) {
            if (entry.getValue() instanceof String) {
                sb.append(entry.getKey() + "=" + entry.getValue() + "&");
            } else if (entry.getValue() instanceof String[]) {
                String[] values = (String[]) entry.getValue();
                for (String value : values) {
                    sb.append(entry.getKey() + "=" + value + "&");
                }
            }
        }
        StringUtils.trim(sb, '&');
        return sb.toString();
    }

    public String getProxyHostPort() {
        return proxyHostPort;
    }

    public void setProxyHostPort(String proxyHostPort) {
        // TODO
         logger.info("setProxyHostPort:" + proxyHostPort);

        String host = proxyHostPort;
        int port = 80;
        int pos = proxyHostPort.indexOf(':');
        if (pos > 0) {
            host = proxyHostPort.substring(0, pos);
            port = Integer.parseInt(proxyHostPort.substring(pos + 1).trim());
        }
        client.getHostConfiguration().setProxy(host, port);
    }

    public HttpClient getClient() {
        return client;
    }

    public void setAccessLog(AccessLog accessLog) {
        this.accessLog = accessLog;
    }

    @Override
    public String get(String url) {
        return get(url, DEFAULT_CHARSET);
    }

    public String get(String url, Map<String, String> headers) {
        return get(url, headers, DEFAULT_CHARSET);
    }

    public String get(String url, Map<String, String> headers, Map<String, String> ps) {
        return get(url, headers, DEFAULT_CHARSET, ps);
    }

    @Override
    public String get(String url, String charset) {
        return get(url, null, charset);
    }

    public String get(String url, Map<String, String> headers, String charset) {
        return get(url, headers, charset, Collections.EMPTY_MAP);
    }

    public String get(String url, Map<String, String> headers, String charset, Map<String, String> ps) {

        HttpMethod get = new GetMethod(url);
        HttpMethodParams params = new HttpMethodParams();
        params.setContentCharset(charset);
        params.setUriCharset(charset);
        if (ps != null) {
            NameValuePair[] nv = new NameValuePair[ps.size()];
            int i = 0;
            for (Map.Entry<String, String> entry : ps.entrySet()) {
                nv[i++] = new NameValuePair(entry.getKey(), entry.getValue());
            }
            get.setQueryString(nv);
        }
        get.setParams(params);
        addHeader(get, headers);
        return executeMethod(url, get, null, charset, 1);
    }

    public String getAsync(final String url) {
        Future<String> future = httpPool.submit(new Callable<String>() {
            public String call() throws Exception {

                return get(url);
            }
        });
        try {
            return future.get(this.soTimeOut, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // TODO
            // warn("getAsync error url:"+url+" msg:"+e.getMessage());
            return "";
        }
    }

    public Future<String> getAsyncFuture(final String url) {
        Future<String> future = httpPool.submit(new Callable<String>() {
            public String call() throws Exception {

                return get(url);
            }
        });
        return future;
    }

    public String postAsync(final String url, final Map<String, ?> nameValues) {
        Future<String> future = httpPool.submit(new Callable<String>() {
            public String call() throws Exception {
                return post(url, nameValues);
            }
        });
        try {
            return future.get(this.soTimeOut, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // TODO
            // warn(format("getAsync error url:%s post:%s msg:%s",url,mapToString(nameValues),e.getMessage()));
            return "";
        }
    }

    public Future<String> postAsyncFuture(final String url, final Map<String, ?> nameValues) {
        Future<String> future = httpPool.submit(new Callable<String>() {
            public String call() throws Exception {
                return post(url, nameValues);
            }
        });
        return future;
    }

    public byte[] getByte(String url) {
        return getByte(url, DEFAULT_SIZE);
    }

    public byte[] getByte(String url, int size) {
        if (size > maxSize) {
            size = maxSize;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        long start = System.currentTimeMillis();
        HttpMethod get = new GetMethod(url);
        int len = 0;
        try {
            len = doExecuteMethod(get, out);
            return out.toByteArray();
        } finally {
            accessLog(System.currentTimeMillis() - start, "GET",
                    get.getStatusLine() != null ? get.getStatusCode() : -1,
                    len, url, "", null);
        }
    }

    @Override
    public String post(String url, Map<String, ?> nameValues) {
        return post(url, nameValues, DEFAULT_CHARSET);
    }

    @Override
    public String post(String url, Map<String, ?> nameValues, String charset) {
        return post(url, nameValues, null, charset);
    }

    public String post(String url, Map<String, ?> nameValues, Map<String, String> headers) {
        return post(url, nameValues, headers, DEFAULT_CHARSET);
    }

    public String post(String url, Map<String, ?> nameValues, Map<String, String> headers, String charset) {
        PostMethod post = new PostMethod(url);
        HttpMethodParams params = new HttpMethodParams();
        params.setContentCharset(charset);
        post.setParams(params);
        addHeader(post, headers);
        if (nameValues != null && !nameValues.isEmpty()) {
            List<NameValuePair> list = new ArrayList<NameValuePair>(
                    nameValues.size());
            for (Map.Entry<String, ?> entry : nameValues.entrySet()) {
                if (entry.getKey() != null && !entry.getKey().isEmpty()) {
                    list.add(new NameValuePair(entry.getKey(), entry
                            .getValue().toString()));
                } else {
                    try {
                        post.setRequestEntity(new StringRequestEntity(entry
                                .getValue().toString(), "text/xml", "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                    }
                }
            }
            if (!list.isEmpty()) {
                post.setRequestBody(list.toArray(new NameValuePair[list.size()]));
            }
        }
        return executeMethod(url, post, mapToString(nameValues), charset, 1);
    }

    public String postMulti(String url, Map<String, Object> nameValues) {
        return postMulti(url, nameValues, DEFAULT_CHARSET);
    }

    public String postMulti(String url, Map<String, Object> nameValues, String charset) {
        PostMethod post = new PostMethod(url);
        Part[] parts = new Part[nameValues.size()];
        if (nameValues != null && !nameValues.isEmpty()) {
            int i = 0;
            for (Map.Entry<String, Object> entry : nameValues.entrySet()) {
                if (entry.getValue() instanceof ByteArrayPart) {
                    ByteArrayPart data = (ByteArrayPart) entry.getValue();
                    parts[i++] = data;
                    continue;
                }
                if (entry.getValue() instanceof FileItem) {
                    FileItem item = (FileItem) entry.getValue();
                    String contentType = item.getContentType();
                    if ("application/octet-stream".equals(contentType)) {
                        contentType = "image/png";
                    }
                    parts[i++] = new ByteArrayPart(item.get(), entry.getKey(),contentType);
                } else {
                    parts[i++] = new StringPart(entry.getKey(), entry.getValue().toString(), "utf-8");
                }
            }
        }
        post.setRequestEntity(new MultipartRequestEntity(parts, post
                .getParams()));
        return executeMethod(url, post, mapToString(nameValues), charset, 1);
    }

    public String postMulti(String url, InputStream in) {
        PostMethod post = new PostMethod(url);
        post.setRequestEntity(new InputStreamRequestEntity(in));
        return executeMethod(url, post, null, DEFAULT_CHARSET, 1);
    }

    public String postMulti(String url, byte[] buf) {
        return this.postMulti(url, new ByteArrayInputStream(buf));
    }

    private int executeMethodBytes(String url, HttpMethod method, ByteArrayOutputStream out, String charset, int times) {
        long start = System.currentTimeMillis();
        int len = 0;
        int flag = 0;
        while (flag < times) {
            try {
                len = doExecuteMethod(method, out);
                return len;
            } catch (Exception e) {
                flag++;
                if (flag >= times) {
                    throw e;
                } else {
                    logger.info("retry " + flag + " for:" + url);
                }
            } finally {
                accessLog(System.currentTimeMillis() - start, method.getName(),
                        method.getStatusLine() != null ? method.getStatusCode() : -1,
                        len, url, method.getQueryString(), null, "-");
            }
        }
        return 0;
    }

    private String executeMethod(String url, HttpMethod method, String postString, String charset, int times) {
        String result = null;
        long start = System.currentTimeMillis();
        int len = 0;
        int flag = 0;
        while (flag < times) {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                len = doExecuteMethod(method, out);
                try {
                    result = new String(out.toByteArray(), charset);
                } catch (UnsupportedEncodingException e) {
                    result = new String(out.toByteArray());
                }
                flag++;
                return result;
            } catch (RuntimeException e) {
                flag++;
                if (flag >= times) {
                    this.exceptionHandler.handle(e);
                }
            } finally {
                accessLog(System.currentTimeMillis() - start, method.getName(),
                        method.getStatusLine() != null ? method.getStatusCode() : -1,
                        len, url, method.getQueryString(), postString, result);
            }
        }
        return null;
    }

    private int doExecuteMethod(HttpMethod httpMethod, OutputStream out) throws ApiHttpClientExcpetion {
        long start = System.currentTimeMillis();
        int readLen = 0;
        try {
            addRemoteInvokerHeader(httpMethod);
            client.executeMethod(httpMethod);
            if (System.currentTimeMillis() - start > this.soTimeOut) {
                throw new ReadTimeOutException(format(
                        "executeMethod so timeout time:%s soTimeOut:%s",
                        (System.currentTimeMillis() - start), soTimeOut));
            }
            int code = httpMethod.getStatusCode();
            //自动处理重定向
            //TODO 检查重定向次数
            if (code == HttpStatus.SC_MOVED_PERMANENTLY || code == HttpStatus.SC_MOVED_TEMPORARILY) {
                Header locationHeader = httpMethod.getResponseHeader("location");
                if (locationHeader != null) {
                    String redirectLocation = locationHeader.getValue();
                    if (!StringUtils.isBlank(redirectLocation)) {
                        logger.info("auto redirect to:" + redirectLocation);
                        httpMethod.setURI(new HttpURL(redirectLocation));
                        return doExecuteMethod(httpMethod, out);
                    }
                }
            }
            InputStream in = httpMethod.getResponseBodyAsStream();
            byte[] b = new byte[10240];
            int len = 0;
            while ((len = in.read(b)) > 0) {
                if (System.currentTimeMillis() - start > this.soTimeOut) {
                    throw new ReadTimeOutException(format(
                            "read so timeout time:%s soTimeOut:%s",
                            (System.currentTimeMillis() - start), soTimeOut));
                }
                out.write(b, 0, len);
                readLen += len;
                if (readLen > maxSize) {
                    throw new SizeException(
                            format("size too big size:%s maxSize:%s", readLen,
                                    maxSize));
                }
            }
            in.close();
        } catch (ApiHttpClientExcpetion ex) {
            // TODO
            // warn(format("ApiHttpClientExcpetion url:%s message:%s",
            // getHttpMethodURL(httpMethod),
            // ex.getMessage()));
            throw ex;
        } catch (Exception ex) {
            // TODO
            // warn(format("ApacheHttpClient.doExecuteMethod error! msg:%s",ex.getMessage()));
            throw new ApiHttpClientExcpetion(ex.getMessage(), ex);
        } finally {
            httpMethod.releaseConnection();
        }
        return readLen;
    }

    private String getHttpMethodURL(HttpMethod httpMethod) {
        try {
            return httpMethod.getURI().toString();
        } catch (URIException e) {
            return "";
        }
    }

    public <T> T get(String url, ResultConvert<T> c) {
        String ret = get(url);
        return c.convert(url, null, ret);
    }

    public <T> T get(String url, String charset, ResultConvert<T> c) {
        String ret = get(url, charset);
        return c.convert(url, null, ret);
    }

    public <T> T post(String url, Map<String, ?> nameValues, ResultConvert<T> c) {
        String ret = post(url, nameValues);
        return c.convert(url, mapToString(nameValues), ret);
    }

    public <T> T post(String url, Map<String, ?> nameValues, String charset, ResultConvert<T> c) {
        String ret = post(url, nameValues, charset);
        return c.convert(url, mapToString(nameValues), ret);
    }

    public <T> T postMulti(String url, Map<String, Object> nameValues, String charset, ResultConvert<T> c) {
        String ret = postMulti(url, nameValues, charset);
        return c.convert(url, mapToString(nameValues), ret);
    }

    public RequestBuilder buildGet(String url) {
        HttpMethod get = new GetMethod(url);
        HttpClientRequestBuilder ret = new HttpClientRequestBuilder(url, get, false);
        return ret;
    }

    public RequestBuilder buildPost(String url) {
        PostMethod post = new PostMethod(url);
        HttpClientRequestBuilder ret = new HttpClientRequestBuilder(url, post, false);
        return ret;
    }

    private void accessLog(long time, String method, int status, int len,
                           String uri, String queryString, String post) {
        accessLog(time, method, status, len, uri, queryString, post, "-");
    }

    private void accessLog(long time, String method, int status, int len,
                           String uri, String queryString, String post, String ret) {
        String url = null;
        if (StringUtils.isEmpty(queryString)) {
            url = uri;
        } else if (uri.contains("?")) {
            url = uri.substring(0, uri.indexOf("?") + 1) + queryString;
        } else {
            url = uri + "?" + queryString;
        }
        if (time > soTimeOut) {
            // TODO
            // logger.fire("HTTP " + uri + " Error:" + time);
        }
        if (accessLog != null) {
            try {
                accessLog.accessLog(time, method, status, len, url, post, ret);
            } catch (Exception e) {
                // TOOD
                // warn("error accessLog", e);
            }
        }
    }

    private void addRemoteInvokerHeader(HttpMethod httpMethod) {
        httpMethod.setRequestHeader("X-Remote-API-Invoker", "openapi");
    }

    @Override
    public void setExceptionHandler(ExceptionHandler handler) {
        this.exceptionHandler = handler;
    }

    public static class ApiHttpClientExcpetion extends RuntimeException {
        public ApiHttpClientExcpetion(String msg) {
            super(msg);
        }

        public ApiHttpClientExcpetion(String msg, Exception e) {
            super(msg);
        }
    }

    public static class ReadTimeOutException extends ApiHttpClientExcpetion {
        public ReadTimeOutException(String msg) {
            super(msg);
        }
    }

    public static class SizeException extends ApiHttpClientExcpetion {
        public SizeException(String msg) {
            super(msg);
        }
    }

    public class HttpClientRequestBuilder implements RequestBuilder {
        private String url;
        private HttpMethod method;

        private boolean isBlock = false;
        private Map<String, String[]> queryParam = new HashMap<String, String[]>();
        private Map<String, String[]> bodyStringParam = new LinkedHashMap(16);
        private Map<String, Object> bodyBinParam = new LinkedHashMap(16);
        private byte[] body;
        private String charset;

        public HttpClientRequestBuilder(String url, HttpMethod method, boolean isBlock) {
            this.url = url;
            this.method = method;
            this.isBlock = isBlock;
            HttpMethodParams params = new HttpMethodParams();
            params.setContentCharset(DEFAULT_CHARSET);
            params.setUriCharset(DEFAULT_CHARSET);
            method.setParams(params);
            charset = DEFAULT_CHARSET;
            if (isBlock) {
                // debug("blockResource url="+url);
            }
        }

        @Override
        public RequestBuilder withCharset(String charset) {
            this.method.getParams().setContentCharset(charset);
            this.method.getParams().setUriCharset(charset);
            this.charset = charset;
            return this;
        }

        @Override
        public RequestBuilder withParam(Map<String, ?> param) {
            if (param == null) {
                return this;
            }
            for (Map.Entry<String, ?> entry : param.entrySet()) {
                withParam(entry.getKey(), entry.getValue());
            }
            return this;
        }

        @Override
        public RequestBuilder withParam(boolean condition, String key, Object value) {
            if (condition) {
                return withParam(key, value);
            }
            return this;
        }

        @Override
        public RequestBuilder withParam(String key, Object value) {
            if (value == null) {
                return this;
            }
            boolean isQueryParam = isQueryParam();
            Map<String, String[]> param = isQueryParam ? queryParam : bodyStringParam;
            if (value instanceof String) {
                addParameter(key, (String) value, param);
            } else if (value instanceof String[]) {
                addParameter(key, (String[]) value, param);
            } else if (value.getClass().isPrimitive()) {
                addParameter(key, value.toString(), param);
            } else if (Number.class.isAssignableFrom(value.getClass()) || value.getClass() == Boolean.class || value.getClass() == Character.class) {
                addParameter(key, value.toString(), param);
            } else if (value.getClass().isArray() && !(value instanceof byte[])) {
                int len = Array.getLength(value);
                for (int i = 0; i < len; i++) {
                    withParam(key, Array.get(value, i));
                }
            } else if (value instanceof Collection) {
                Iterator iter = ((Collection) value).iterator();
                while (iter.hasNext()) {
                    withParam(key, iter.next());
                }
            } else if (value instanceof Future) {
                Future future = (Future) value;
                try {
                    Object v = future.get(ApacheHttpClient.this.soTimeOut, TimeUnit.MILLISECONDS);
                    this.withParam(key, v);
                } catch (Exception e) {
                    throw new ApiHttpClientExcpetion("read param timeout", e);
                }
            } else {
                if (isQueryParam) {
                    // warn(format(
                    // "HttpClientRequestBuilder.withParam unsupport url:%s type:%s",
                    // url, value.getClass().getSimpleName()));
                    addParameter(key, value.toString(), queryParam);
                } else {
                    bodyBinParam.put(key, value);
                }
            }
            return this;
        }

        public RequestBuilder withHeader(boolean condition, String key, String value) {
            if (condition) {
                return withHeader(key, value);
            }
            return this;
        }

        public RequestBuilder withHeader(String key, String value) {
            if(logger.isDebugEnabled()){
                logger.debug("httpclient","withHeader "+key+":"+value);
            }
            method.setRequestHeader(key, value);
            return this;
        }

        @Override
        public RequestBuilder withHeader(Map<String, String> header) {
            if (header != null) {
                for (Map.Entry<String, String> entry : header.entrySet())
                    method.addRequestHeader(entry.getKey(), entry.getValue());
            }
            return this;
        }

        @Override
        public RequestBuilder withBasicAuth(String username, String password) {
            method.addRequestHeader("Authorization", "Basic "
                    + new String(org.apache.commons.codec.binary.Base64.encodeBase64(
                    (username + ":" + password).getBytes(), false)));
            return this;
        }

        @Override
        public <T> T execute(ResultConvert<T> convert) {
            return convert.convert(url, mapToString(bodyStringParam), execute());
        }

        @Override
        public <T> T execute(ResultConvert<T> convert, int times) {
            return convert.convert(url, mapToString(bodyStringParam), execute(times));
        }

        @Override
        public <T> T execute(ResponseConvert<T> convert) {
            return convert.convert(executeAsResponse());
        }

        @Override
        public <T> T execute(ResponseConvert<T> convert, int times) {
            return convert.convert(executeAsResponse(times));
        }

        @Override
        public <T> Future<T> executeAsync(final ResultConvert<T> convert) {
            Future<T> future = httpPool.submit(new Callable<T>() {
                public T call() throws Exception {
                    return execute(convert);
                }
            });
            return future;
        }

        @Override
        public <T> Future<T> executeAsync(final ResultConvert<T> convert, final int times) {
            Future<T> future = httpPool.submit(new Callable<T>() {
                public T call() throws Exception {
                    return execute(convert, times);
                }
            });
            return future;
        }

        @Override
        public <T> Future<T> executeAsync(final ResponseConvert<T> convert) {
            Future<T> future = httpPool.submit(new Callable<T>() {
                public T call() throws Exception {
                    return execute(convert);
                }
            });
            return future;
        }

        @Override
        public <T> Future<T> executeAsync(final ResponseConvert<T> convert, final int times) {
            Future<T> future = httpPool.submit(new Callable<T>() {
                public T call() throws Exception {
                    return execute(convert, times);
                }
            });
            return future;
        }

        @Override
        public String execute() {
            if (isBlock) {
                return "";
            }
            setQuery();
            setBody();
            String post = null;
            if (bodyStringParam.size() > 0) {
                post = mapToString(bodyStringParam);
            }
            return executeMethod(url, method, post, this.charset, 1);
        }

        @Override
        public String execute(int times) {
            if (isBlock) {
                return "";
            }
            setQuery();
            setBody();
            String post = null;
            if (bodyStringParam.size() > 0) {
                post = mapToString(bodyStringParam);
            }
            return executeMethod(url, method, post, this.charset, times);
        }

        public JSONObject executeAsJSONObject() {
            return this.execute(new JSONObjectResponseConvert());
        }

        @Override
        public byte[] executeByte() {
            if (isBlock) {
                return new byte[]{};
            }
            setQuery();
            setBody();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            executeMethodBytes(url, method, out, this.charset, 1);
            return out.toByteArray();
        }

        // TODO 所有execute方法都基于 executeAsResponse 实现
        @Override
        public HttpResponse executeAsResponse() {
            setQuery();
            setBody();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            executeMethodBytes(url, method, out, this.charset, 1);
            int status = method.getStatusCode();
            Header[] headers = method.getResponseHeaders();
            Map<String, String> headerMap = new HashMap<String, String>();
            for (Header header : headers) {
                headerMap.put(header.getName(), header.getValue());
            }
            return new HttpResponse(status, headerMap, out.toByteArray());
        }

        @Override
        public HttpResponse executeAsResponse(int times) {
            setQuery();
            setBody();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            executeMethodBytes(url, method, out, this.charset, times);
            int status = method.getStatusCode();
            Header[] headers = method.getResponseHeaders();
            Map<String, String> headerMap = new HashMap<String, String>();
            for (Header header : headers) {
                headerMap.put(header.getName(), header.getValue());
            }
            return new HttpResponse(status, headerMap, out.toByteArray());
        }

        @Override
        public Future<HttpResponse> executeAsResponseAsync() {
            Future<HttpResponse> future = httpPool.submit(new Callable<HttpResponse>() {
                public HttpResponse call() throws Exception {
                    return executeAsResponse();
                }
            });
            return future;
        }

        public Future<String> executeAsync() {
            Future<String> future = httpPool.submit(new Callable<String>() {
                public String call() throws Exception {
                    return execute();
                }
            });
            return future;
        }

        public String executeAsyncString() {
            Future<String> future = httpPool.submit(new Callable<String>() {
                public String call() throws Exception {
                    return execute();
                }
            });
            try {
                return future.get(ApacheHttpClient.this.soTimeOut, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                // warn(format("getAsync error url:%s post:%s msg:%s",url,mapToString(bodyStringParam),e.getMessage()));
                return "";
            }
        }

        private void setQuery() {
            if (queryParam.size() == 0) {
                return;
            }
            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, String[]> entry : queryParam.entrySet()) {
                for (String item : entry.getValue()) {
                    sb.append(urlEncode(entry.getKey(), this.charset) + "=" + urlEncode(item, this.charset) + "&");
                }
            }
            StringUtils.trim(sb, '&');
            method.setQueryString(sb.toString());
        }

        private void setBody() {
            if (bodyBinParam.size() == 0 && bodyStringParam.size() == 0 && this.body == null) {
                return;
            }
            if (this.body != null) {
                this.setFullBody();
            } else if (bodyBinParam.size() > 0) {
                setMultiBody();
            } else {
                setStringBody();
            }
        }

        private void setFullBody() {
            PostMethod postMethod = (PostMethod) method;
            postMethod.setRequestEntity(new InputStreamRequestEntity(new ByteArrayInputStream(this.body)));
        }

        private void setStringBody() {
            PostMethod postMethod = (PostMethod) method;
            List<NameValuePair> list = new ArrayList<NameValuePair>(
                    bodyStringParam.size());
            for (Map.Entry<String, String[]> entry : bodyStringParam.entrySet()) {
                String[] values = entry.getValue();
                for (String value : values) {
                    list.add(new NameValuePair(entry.getKey(), value));
                }
            }
            if (!list.isEmpty()) {
                postMethod.setRequestBody(list.toArray(new NameValuePair[list
                        .size()]));
            }
        }

        private void setMultiBody() {
            PostMethod postMethod = (PostMethod) method;
            List<Part> partList = new ArrayList<Part>();
            for (Map.Entry<String, String[]> entry : bodyStringParam.entrySet()) {
                for (String value : entry.getValue()) {
                    partList.add(new StringPart(entry.getKey(), value, DEFAULT_CHARSET));
                }
            }
            for (Map.Entry<String, Object> entry : bodyBinParam.entrySet()) {
                if (entry.getValue() instanceof ByteArrayPart) {
                    ByteArrayPart data = (ByteArrayPart) entry.getValue();
                    partList.add(data);
                } else if (entry.getValue() instanceof FileItem) {
                    FileItem item = (FileItem) entry.getValue();
                    String contentType = item.getContentType();
                    if ("application/octet-stream".equals(contentType)) {
                        contentType = "image/png";
                    }
                    partList.add(new ByteArrayPart(item.get(), entry.getKey(),
                            contentType));
                } else if (entry.getValue() instanceof byte[]) {
                    ByteArrayPart data = new ByteArrayPart((byte[]) entry.getValue(), entry.getKey(), "application/octet-stream");
                    partList.add(data);
                } else if (entry.getValue() instanceof ByteFileUpload) {
                    ByteFileUpload byteFileUpload = (ByteFileUpload) entry.getValue();
                    ByteArrayPart data = new ByteArrayPart(byteFileUpload.getUploadData(), entry.getKey(), byteFileUpload.getFileName(), "application/octet-stream");
                    partList.add(data);
                } else {
                    // warn(format(
                    // "HttpClientRequestBuilder.setMultiBody unsupport url:%s type:%s",
                    // url, entry.getValue().getClass().getSimpleName()));
                    partList.add(new StringPart(entry.getKey(), entry
                            .getValue().toString(), "utf-8"));
                }
            }
            postMethod.setRequestEntity(new MultipartRequestEntity(partList
                    .toArray(new Part[]{}), postMethod.getParams()));
        }

        public void addParameter(String name, String value, Map<String, String[]> paramMap) {
            addParameter(name, new String[]{value}, paramMap);
        }

        public void addParameter(String name, String[] values, Map<String, String[]> paramMap) {
            // Assert.notNull(name, "Parameter name must not be null");
            String[] oldArr = (String[]) paramMap.get(name);
            if (oldArr != null) {
                String[] newArr = new String[oldArr.length + values.length];
                System.arraycopy(oldArr, 0, newArr, 0, oldArr.length);
                System.arraycopy(values, 0, newArr, oldArr.length, values.length);
                paramMap.put(name, newArr);
            } else {
                paramMap.put(name, values);
            }
        }

        private void addParameters(Map params) {
            // Assert.notNull(params, "Parameter map must not be null");
            for (Iterator it = params.keySet().iterator(); it.hasNext(); ) {
                Object key = it.next();
                // Assert.isInstanceOf(String.class, key,
                // "Parameter map key must be of type [" +
                // String.class.getName() + "]");
                Object value = params.get(key);
                if (value instanceof String) {
                    this.addParameter((String) key, (String) value, queryParam);
                } else if (value instanceof String[]) {
                    this.addParameter((String) key, (String[]) value, queryParam);
                } else {
                    throw new IllegalArgumentException("Parameter map value must be single value " +
                            " or array of type [" + String.class.getName() + "]");
                }
            }
        }

        private boolean isQueryParam() {
            return method instanceof GetMethod || method instanceof DeleteMethod;
        }

        @Override
        public RequestBuilder withBody(String value) {
            try {
                return this.withBody(value.getBytes(DEFAULT_CHARSET));
            } catch (UnsupportedEncodingException e) {
                return this.withBody(value.getBytes());
            }
        }

        @Override
        public RequestBuilder withBody(byte[] value) {
            this.body = value;
            return this;
        }

    }
}

