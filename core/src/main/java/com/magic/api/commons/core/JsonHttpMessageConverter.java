package com.magic.api.commons.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.magic.api.commons.core.context.RequestContext;
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
        RequestContext.Result result = RequestContext.getRequestContext().getResult();
        boolean wrap = result.isWrap();
        if ((obj instanceof JSONObject || obj instanceof JSONArray) && wrap) {
            writeJson((Serializable) obj, outputMessage);
        } else if (obj instanceof String) {
            String json = (String) obj;
            if (wrap) {
                json = "{\"apistatus\":1,\"result\":" + json + "}";
            }
            RequestContext.getRequestContext().getRequestLogRecord().setResponse(json);
            OutputStream out = outputMessage.getBody();
            out.write(json.getBytes(Charset.forName("UTF-8")));
        } else {
            super.writeInternal(obj, outputMessage);
        }
    }

    /**
     * 写出JSON
     * @param json  JSON
     * @param outputMessage HttpOutputMessage
     * @throws IOException IOException
     */
    private void writeJson(Serializable json, HttpOutputMessage outputMessage) throws IOException {
        JSONObject result = new JSONObject();
        result.put("apistatus", 1);
        result.put("result", json);
        RequestContext.getRequestContext().getRequestLogRecord().setResponse(result);
        super.writeInternal(result, outputMessage);
    }
}
