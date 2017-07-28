package com.magic.api.commons.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.context.RequestContext;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 框架级JSON包裹，包括对xss的处理
 */
public class JsonHttpMessageConverter extends FastJsonHttpMessageConverter {

    /**
     * @param obj
     * @param outputMessage
     * @throws IOException
     * @throws HttpMessageNotWritableException
     */
    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        RequestContext.Result result = RequestContext.getRequestContext().getResult();
        boolean wrap = result.isWrap();

        if ((obj instanceof Map) && wrap) {
            disposeMap((Map) obj, outputMessage);
        } else if ((obj instanceof List) && wrap) {
            disposeList((JSONArray) obj, outputMessage);
        } else if (obj instanceof String) {
            disposeString((String) obj, outputMessage, wrap);
        } else {
            super.writeInternal(obj, outputMessage);
        }

    }

    /**
     * 对Map返回结果进行处理
     *
     * @param obj
     * @param outputMessage
     * @throws IOException
     */
    private void disposeMap(Map obj, HttpOutputMessage outputMessage) throws IOException {
        walkMap(obj);
        JSONObject result = new JSONObject();
        result.put("apistatus", 1);
        result.put("result", obj);
        RequestContext.getRequestContext().getRequestLogRecord().setResponse(result);
        super.writeInternal(result, outputMessage);
    }

    /**
     * string
     *
     * @param strResult
     * @param outputMessage
     * @param wrap
     * @throws IOException
     */
    private void disposeString(String strResult, HttpOutputMessage outputMessage, boolean wrap) throws IOException {
        boolean opResult = disposeStringResult(strResult, outputMessage, wrap);
//        boolean opResult = false;
        if (!opResult) {
            StringBuilder outputResult = new StringBuilder();
            if (wrap) {
                outputResult.append("{\"apistatus\":1,\"result\":").append(strResult).append("}");
            } else {
                outputResult.append(strResult);
            }

            RequestContext.getRequestContext().getRequestLogRecord().setResponse(outputResult);
            OutputStream out = outputMessage.getBody();
            out.write(outputResult.toString().getBytes(Charset.forName("UTF-8")));
        }

    }

    private boolean disposeStringResult(String strResult, HttpOutputMessage outputMessage, boolean wrap) {
        try {
            if (wrap) {
                Object object = JSON.parse(strResult);
                if (object instanceof Map) {
                    disposeMap((Map) object, outputMessage);
                    return true;
                } else if (object instanceof List) {
                    disposeList((List) object, outputMessage);
                    return true;
                } else if (object instanceof String) {
                    String escapeString = getEscapeString((String) object);
                    StringBuilder outputResult = new StringBuilder();
                    outputResult.append("{\"apistatus\":1,\"result\":").append(escapeString).append("}");
                    RequestContext.getRequestContext().getRequestLogRecord().setResponse(outputResult);
                    OutputStream out = outputMessage.getBody();
                    out.write(outputResult.toString().getBytes(Charset.forName("UTF-8")));
                    return true;
                }
            }
        } catch (Exception e) {
            ApiLogger.error("disposeStringResult::error", e);
        }
        return false;
    }

    private void disposeList(List obj, HttpOutputMessage outputMessage) throws IOException {
        walkList(obj);
        JSONObject result = new JSONObject();
        result.put("apistatus", 1);
        result.put("result", obj);
        RequestContext.getRequestContext().getRequestLogRecord().setResponse(result);
        super.writeInternal(result, outputMessage);
    }

    /**
     * map层级遍历
     *
     * @param obj
     */
    private static void walkMap(Map<String, Object> obj) {
        for (Map.Entry<String, Object> entry : obj.entrySet()) {
            parseEntry(entry);
        }
    }

    /**
     * map entry处理,不一定能覆盖到所有的类型,有问题需要修改
     *
     * @param entry
     */
    private static void parseEntry(Map.Entry<String, Object> entry) {
        Object value = entry.getValue();
        if (value instanceof String) {
            String escapeString = getEscapeString((String) value);
            entry.setValue(escapeString);
        } else if (value instanceof Map) {
            walkMap((Map<String, Object>) value);
        } else if (value instanceof List) {
            walkList((List) value);
        } else if (value instanceof Set) {
            walkSet((Set) value);
        }
    }

    /**
     * 减少耗时的操作
     */
    private static Map<String, List> REPLACE_COLLECTIONS =
            new HashMap() {
                {
                    this.put("<script>", new ArrayList() {
                        {
                            this.add(StringEscapeUtils.escapeHtml4("<script>"));
                            this.add(Pattern.compile("<script>"));
                        }
                    });
                    this.put("</script>", new ArrayList() {
                        {
                            this.add(StringEscapeUtils.escapeHtml4("</script>"));
                            this.add(Pattern.compile("</script>"));
                        }
                    });
                }
            };

    private static final int INDEX_ESCAPE_PLACE = 0;
    private static final int INDEX_PATTHRN = 1;

    /**
     * 处理xss，不使用工具是因为客户端来不及修改，只屏蔽<scrpit>和</script>
     *
     * @param value
     * @return
     */
    private static String getEscapeString(String value) {
//        return StringEscapeUtils.escapeHtml4(value);
        for (Map.Entry<String, List> entry : REPLACE_COLLECTIONS.entrySet()) {
            List holder = entry.getValue();
            String escapeString = (String) holder.get(INDEX_ESCAPE_PLACE);
            Pattern pattern = (Pattern) holder.get(INDEX_PATTHRN);
            value = pattern
                    .matcher(value).replaceAll(escapeString);
        }
        return value;
    }

    /**
     * set 处理,不一定能覆盖到所有的类型,有问题需要修改
     *
     * @param set
     */
    private static void walkSet(Set set) {
        for (Object value : set) {
            if (value instanceof String) {
                String escapeString = getEscapeString((String) value);
                set.remove(value);
                set.add(escapeString);
            } else if (value instanceof Map) {
                walkMap((Map<String, Object>) value);
            } else if (value instanceof List) {
                walkList((List) value);
            } else if (value instanceof Set) {
                walkSet((Set) value);
            }
        }
    }

    /**
     * list 处理,不一定能覆盖到所有的类型,有问题需要修改
     *
     * @param
     */
    private static void walkList(List list) {
        for (int i = 0; i < list.size(); ++i) {
            Object value = list.get(i);
            if (value instanceof String) {
                String escapeString = getEscapeString((String) value);
                list.set(i, escapeString);
            } else if (value instanceof Map) {
                walkMap((Map<String, Object>) value);
            } else if (value instanceof List) {
                walkList((List) value);
            }
        }
    }


    public static void main(String[] args) {
        System.out.println(StringEscapeUtils.escapeJson("\"<script>alert()</script>"));
        System.out.println(StringEscapeUtils.escapeHtml3("\"<script>alert()</script>"));
        System.out.println(StringEscapeUtils.escapeHtml4("\"<script>alert()</script>"));
        System.out.println(StringEscapeUtils.escapeJava("\"<script>alert()</script>"));
        System.out.println(StringEscapeUtils.escapeXml11("\"<script>alert()</script>"));
        System.out.println(StringEscapeUtils.escapeJson("\"<script>alert()</script>"));
        String s = "{\"ssss\":1}";
        String s1 = "[\"ssss\",\"sssss\"]";
        JSONObject jsonObject = JSONObject.parseObject(s);
        JSONArray jsonArray = JSONObject.parseArray(s1);
        System.out.println(jsonObject instanceof Map);

        String data = "123456";
        Object object = JSON.parse(data);
        if (object instanceof Map) {
            System.out.println("111");
            walkMap((Map<String, Object>) object);
        } else if (object instanceof List) {
            System.out.println("1123");
            walkList((List) object);
        }
        System.out.println(JSON.toJSONString(object));
    }

}
