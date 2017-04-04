package com.magic.api.commons;

import com.alibaba.fastjson.JSONObject;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Future;

public interface ApiHttpClient {

    String get(String url);

    /**
     * 异步方式get
     *
     * @param url
     * @return
     */
    String getAsync(String url);

    Future<String> getAsyncFuture(String url);

    /**
     * ResultConvert 转换字符串为对象方式，如json.
     *
     * @param <T>
     * @param url
     * @param c
     * @return
     */
    <T> T get(String url, ResultConvert<T> c);

    String get(String url, String charset);

    byte[] getByte(String url);

    byte[] getByte(String url, int size);

    String post(String url, Map<String, ?> nameValues);

    String postAsync(String url, Map<String, ?> nameValues);

    Future<String> postAsyncFuture(String url, Map<String, ?> nameValues);

    <T> T post(String url, Map<String, ?> nameValues, ResultConvert<T> c);

    String post(String url, Map<String, ?> nameValues, String charset);

    <T> T post(String url, Map<String, ?> nameValues, String charset, ResultConvert<T> c);

    /**
     * Map<String, Object> nameValues value type support
     * ByteArrayPart,FileItem,String
     *
     * @param url
     * @param nameValues
     * @param charset
     * @return
     */
    String postMulti(String url, Map<String, Object> nameValues, String charset);

    String postMulti(String url, Map<String, Object> nameValues);

    /**
     * 直接将inputstream 作为 body，发送到服务端
     *
     * @param url
     * @param in
     * @return
     */
    String postMulti(String url, InputStream in);

    /**
     * @param url
     * @param buf
     * @return
     * @see #postMulti(String, InputStream)
     */
    String postMulti(String url, byte[] buf);

    RequestBuilder buildGet(String url);

    RequestBuilder buildPost(String url);

    public interface ResultConvert<T> {
        T convert(String url, String post, String result);
    }

    public interface ResponseConvert<T> {
        T convert(HttpResponse response);
    }

    public static final StringResponseConvert STRING_CONVERT = new StringResponseConvert();

    public class StringResponseConvert implements ResponseConvert<String> {

        @Override
        public String convert(HttpResponse response) {
            return response.getBodyAsString();
        }

    }

    public class JSONObjectResponseConvert implements ResponseConvert<JSONObject> {

        @Override
        public JSONObject convert(HttpResponse response) {
            String str = response.getBodyAsString();
            return JSONObject.parseObject(str);
        }

    }

    public static final ByteResponseConvert BYTE_CONVERT = new ByteResponseConvert();

    public class ByteResponseConvert implements ResponseConvert<byte[]> {

        @Override
        public byte[] convert(HttpResponse response) {
            return response.getBody();
        }

    }

    void setAccessLog(AccessLog accessLog);

    void setExceptionHandler(ExceptionHandler handler);

    public interface AccessLog {
        void accessLog(long time, String method, int status, int len, String url,
                       String post, String ret);
    }

    public interface RequestBuilder {
        /**
         * 设置请求的uri charset和content charset
         *
         * @param charset
         * @return
         */
        RequestBuilder withCharset(String charset);

        RequestBuilder withParam(Map<String, ?> param);

        RequestBuilder withParam(boolean condition, String key, Object value);

        RequestBuilder withParam(String key, Object value);

        RequestBuilder withHeader(Map<String, String> header);

        RequestBuilder withHeader(boolean condition, String key, String value);

        RequestBuilder withHeader(String key, String value);

        RequestBuilder withBasicAuth(String username, String password);

        RequestBuilder withBody(String value);

        RequestBuilder withBody(byte[] value);

        <T> T execute(ResultConvert<T> convert);

        <T> T execute(ResultConvert<T> convert, int times);

        <T> T execute(ResponseConvert<T> convert);

        <T> T execute(ResponseConvert<T> convert, int times);

        <T> Future<T> executeAsync(ResultConvert<T> convert);

        <T> Future<T> executeAsync(ResultConvert<T> convert, int times);

        <T> Future<T> executeAsync(ResponseConvert<T> convert);

        <T> Future<T> executeAsync(ResponseConvert<T> convert, int times);

        String execute();

        HttpResponse executeAsResponse();

        HttpResponse executeAsResponse(int times);

        Future<HttpResponse> executeAsResponseAsync();

        String execute(int times);

        JSONObject executeAsJSONObject();

        Future<String> executeAsync();

        String executeAsyncString();

        byte[] executeByte();
    }

    public interface ExceptionHandler {
        public void handle(RuntimeException e);
    }

}



