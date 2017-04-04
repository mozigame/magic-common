package com.magic.api.commons.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.core.exception.NotPresentRequiredParamException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;

public class JsonHttpMessageConverter extends FastJsonHttpMessageConverter {

    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        RequestContext.Result result = RequestContext.getRequestContext().getResult();
        boolean wrap = result.isWrap();
        int apistatus = result.getApistatus();
        String errorMsg = result.getErrorMsg();
        if ((obj instanceof JSONObject || obj instanceof JSONArray) && wrap) {
            writeJson((Serializable) obj, outputMessage, apistatus);
        } else if (obj instanceof String) {
            /*String res = (String) obj;
            RequestContext.getRequestContext().getResult().setResult(new String(res.getBytes("utf-8")));*/
            StringBuilder res = new StringBuilder("{\"apistatus\":").append(apistatus);
            if (!StringUtils.isEmpty(errorMsg)){
                res.append(",\"errorMsg\":\"").append(errorMsg).append("\"");
            }
            res.append(",\"result\":").append(obj).append("}");
            String json = wrap ? String.valueOf(res) : ((String) obj);
            RequestContext.getRequestContext().getRequestLogRecord().setResponse(json);
            OutputStream out = outputMessage.getBody();
            out.write(json.getBytes(Charset.forName("UTF-8")));
        } else if (obj instanceof NotPresentRequiredParamException) {
            StringBuffer stringBuilder = new StringBuffer();
            NotPresentRequiredParamException exception = (NotPresentRequiredParamException) obj;
            if (exception != null) {
                String errorMessage = exception.getMessage();
                if (StringUtils.isNotBlank(errorMessage)) {
                    if (errorMessage.contains("Exception:")) {
                        errorMessage = errorMessage.substring(errorMessage.lastIndexOf("Exception:"));
                    }
                    stringBuilder.append(errorMessage);
                }else {
                    stringBuilder.append("缺少参数");
                }
            } else {
                stringBuilder.append("缺少参数");
            }
            writeErrorMsg(stringBuilder.toString(), outputMessage);
        } else {
            super.writeInternal(obj, outputMessage);
        }
    }

    private void writeJson(Serializable json, HttpOutputMessage outputMessage, int apistatus) throws IOException {
        if (1 != apistatus) {
            writeErrorMsg(RequestContext.getRequestContext().getResult().getErrorMsg(), outputMessage);
        }
        JSONObject result = new JSONObject();
        result.put("apistatus", apistatus);
        result.put("result", json);
        RequestContext.getRequestContext().getRequestLogRecord().setResponse(result);
        super.writeInternal(result, outputMessage);
    }

    private void writeErrorMsg(Serializable json, HttpOutputMessage outputMessage) throws IOException {
        JSONObject result = new JSONObject();
        result.put("apistatus", 0);
        result.put("errorMsg", json);
        RequestContext.getRequestContext().getRequestLogRecord().setResponse(result);
        super.writeInternal(result, outputMessage);
    }
}
