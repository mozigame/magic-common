package com.magic.api.commons.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.context.RequestContext;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * 框架级JSON包裹
 */
public class JsonHttpMessageConverter extends FastJsonHttpMessageConverter {

    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        ApiLogger.error("JsonHttpMessageConverter start");
        RequestContext.Result result = RequestContext.getRequestContext().getResult();
        boolean wrap = result.isWrap();
        if ((obj instanceof JSONObject || obj instanceof JSONArray) && wrap) {
            writeJson((Serializable) obj, outputMessage);
        } else if (obj instanceof String) {
            String json = (String) obj;
            if (wrap) {
                json = "{\"apistatus\":1,\"result\":" + json + "}";
            }
            String outputResult = StringEscapeUtils.escapeHtml4(json);
            RequestContext.getRequestContext().getRequestLogRecord().setResponse(outputResult);
            OutputStream out = outputMessage.getBody();
            out.write(outputResult.getBytes(Charset.forName("UTF-8")));
        } else {
            super.writeInternal(obj, outputMessage);
        }
    }

    /**
     * 写出JSON
     *
     * @param json          JSON
     * @param outputMessage HttpOutputMessage
     * @throws IOException IOException
     */
    private void writeJson(Serializable json, HttpOutputMessage outputMessage) throws IOException {
        JSONObject result = new JSONObject();
        result.put("apistatus", 1);
        result.put("result", json);
        String stringResult = JSONObject.toJSONString(result);
        String outputResult = StringEscapeUtils.escapeHtml4(stringResult);
        RequestContext.getRequestContext().getRequestLogRecord().setResponse(outputResult);
        OutputStream out = outputMessage.getBody();
        out.write(outputResult.getBytes(Charset.forName("UTF-8")));
    }


    public static void main(String[] args) {
        System.out.println(StringEscapeUtils.escapeJson("<script>alert()</script>"));
        System.out.println(StringEscapeUtils.escapeHtml4("<script>alert()</script>"));
        System.out.println(StringEscapeUtils.escapeJava("<script>alert()</script>"));
        System.out.println(StringEscapeUtils.escapeXml11("<script>alert()</script>"));
        System.out.println(StringEscapeUtils.escapeJson("<script>alert()</script>"));

    }

}
