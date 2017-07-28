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
                } else if (object instanceof List) {
                    disposeList((List) object, outputMessage);
                }
                return true;
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

        String data = "{\"apistatus\":1,\"result\":{\"count\":50,\"list\":[{\"beginTime\":\"2017-07-28 00:00:00\",\"contentId\":224,\"createTime\":\"2017-07-27 23:05:49\",\"endTime\":\"2017-08-31 00:00:00\",\"id\":391,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-27 23:08:56\",\"reviewUser\":\"worker_7\",\"status\":1,\"supportTerminal\":1,\"title\":\"072502test\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"jonna100\"},{\"beginTime\":\"2017-07-28 00:00:00\",\"contentId\":223,\"createTime\":\"2017-07-27 23:04:51\",\"endTime\":\"2017-08-04 00:00:00\",\"id\":390,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-27 23:09:01\",\"reviewUser\":\"worker_7\",\"status\":1,\"supportTerminal\":1,\"title\":\"最新的优惠，快来参加\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"jonna100\"},{\"beginTime\":\"2017-07-27 00:00:00\",\"contentId\":38,\"createTime\":\"2017-07-27 23:03:57\",\"endTime\":\"2017-08-27 00:00:00\",\"id\":389,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-27 23:04:06\",\"reviewUser\":\"worker_7\",\"status\":0,\"supportTerminal\":1,\"title\":\"轮播图ceshi0728-3\",\"type\":3,\"typeCode\":\"LBT\",\"typeName\":\"轮播图\",\"uploadUser\":\"thomas01\"},{\"beginTime\":\"2017-07-27 00:00:00\",\"contentId\":37,\"createTime\":\"2017-07-27 23:01:03\",\"endTime\":\"2017-08-27 00:00:00\",\"id\":388,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-27 23:03:12\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":1,\"title\":\"轮播图0728-2\",\"type\":3,\"typeCode\":\"LBT\",\"typeName\":\"轮播图\",\"uploadUser\":\"thomas01\"},{\"beginTime\":\"2017-07-28 00:00:00\",\"contentId\":36,\"createTime\":\"2017-07-27 22:59:16\",\"endTime\":\"2017-08-28 00:00:00\",\"id\":387,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-27 23:03:11\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":1,\"title\":\"轮播图0728-1\",\"type\":3,\"typeCode\":\"LBT\",\"typeName\":\"轮播图\",\"uploadUser\":\"thomas01\"},{\"beginTime\":\"2017-07-27 00:00:00\",\"contentId\":222,\"createTime\":\"2017-07-27 09:04:08\",\"endTime\":\"2017-08-05 00:00:00\",\"id\":386,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-27 09:04:48\",\"reviewUser\":\"worker_7\",\"status\":0,\"supportTerminal\":1,\"title\":\"0727test01\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"jonna100\"},{\"beginTime\":\"2017-07-29 00:00:00\",\"contentId\":221,\"createTime\":\"2017-07-27 09:03:17\",\"endTime\":\"2017-08-04 00:00:00\",\"id\":385,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-27 09:04:52\",\"reviewUser\":\"worker_7\",\"status\":1,\"supportTerminal\":1,\"title\":\"0727test1\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"jonna100\"},{\"beginTime\":\"2017-07-23 00:00:00\",\"contentId\":220,\"createTime\":\"2017-07-27 08:54:03\",\"endTime\":\"2017-07-28 00:00:00\",\"id\":384,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":0,\"reviewTime\":\"\",\"reviewUser\":\"\",\"status\":1,\"supportTerminal\":1,\"title\":\"测试看是否新增\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-23 00:00:00\",\"contentId\":219,\"createTime\":\"2017-07-27 08:53:25\",\"endTime\":\"2017-07-28 00:00:00\",\"id\":383,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":0,\"reviewTime\":\"\",\"reviewUser\":\"\",\"status\":1,\"supportTerminal\":1,\"title\":\"第三方\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-24 00:00:00\",\"contentId\":41,\"createTime\":\"2017-07-27 08:45:35\",\"endTime\":\"2017-07-31 00:00:00\",\"id\":382,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":0,\"reviewTime\":\"\",\"reviewUser\":\"\",\"status\":1,\"supportTerminal\":1,\"title\":\"弹窗广告1\",\"type\":7,\"typeCode\":\"TCGG\",\"typeName\":\"弹窗广告\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-27 00:00:00\",\"contentId\":218,\"createTime\":\"2017-07-27 08:35:14\",\"endTime\":\"2017-08-01 00:00:00\",\"id\":381,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":0,\"reviewTime\":\"\",\"reviewUser\":\"\",\"status\":1,\"supportTerminal\":1,\"title\":\"<script>alert(123)</script>\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-18 00:00:00\",\"contentId\":217,\"createTime\":\"2017-07-27 08:16:34\",\"endTime\":\"2017-10-17 00:00:00\",\"id\":380,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":0,\"reviewTime\":\"\",\"reviewUser\":\"\",\"status\":1,\"supportTerminal\":1,\"title\":\"<script>alert(111111)</script>\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-27 00:00:00\",\"contentId\":216,\"createTime\":\"2017-07-27 08:14:59\",\"endTime\":\"2017-08-28 00:00:00\",\"id\":379,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":0,\"reviewTime\":\"\",\"reviewUser\":\"\",\"status\":1,\"supportTerminal\":1,\"title\":\"<script>alert()</script>\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-26 00:00:00\",\"contentId\":215,\"createTime\":\"2017-07-27 08:14:02\",\"endTime\":\"2017-08-01 00:00:00\",\"id\":378,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":0,\"reviewTime\":\"\",\"reviewUser\":\"\",\"status\":1,\"supportTerminal\":1,\"title\":\"<script>alert()</script>\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2018-04-14 00:00:00\",\"contentId\":40,\"createTime\":\"2017-07-27 06:49:58\",\"endTime\":\"2024-04-10 00:00:00\",\"id\":377,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":0,\"reviewTime\":\"\",\"reviewUser\":\"\",\"status\":1,\"supportTerminal\":0,\"title\":\"sdfsfsdfsdfs\",\"type\":7,\"typeCode\":\"TCGG\",\"typeName\":\"弹窗广告\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-27 00:00:00\",\"contentId\":35,\"createTime\":\"2017-07-27 06:28:51\",\"endTime\":\"2017-09-27 00:00:00\",\"id\":376,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-27 06:28:58\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"轮播图0727-4\",\"type\":3,\"typeCode\":\"LBT\",\"typeName\":\"轮播图\",\"uploadUser\":\"thomas01\"},{\"beginTime\":\"2017-07-27 00:00:00\",\"contentId\":34,\"createTime\":\"2017-07-27 04:08:51\",\"endTime\":\"2017-09-27 00:00:00\",\"id\":375,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-27 06:29:15\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"轮播图0727-3\",\"type\":3,\"typeCode\":\"LBT\",\"typeName\":\"轮播图\",\"uploadUser\":\"thomas01\"},{\"beginTime\":\"2017-07-26 00:00:00\",\"contentId\":209,\"createTime\":\"2017-07-26 22:35:35\",\"endTime\":\"2017-08-05 00:00:00\",\"id\":374,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-27 03:02:57\",\"reviewUser\":\"worker_7\",\"status\":0,\"supportTerminal\":0,\"title\":\"0727test\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"jonna100\"},{\"beginTime\":\"2017-07-26 00:00:00\",\"contentId\":33,\"createTime\":\"2017-07-26 22:13:46\",\"endTime\":\"2017-09-26 00:00:00\",\"id\":373,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-26 22:14:02\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"轮播图0727-2\",\"type\":3,\"typeCode\":\"LBT\",\"typeName\":\"轮播图\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-27 00:00:00\",\"contentId\":32,\"createTime\":\"2017-07-26 22:11:05\",\"endTime\":\"2017-09-27 00:00:00\",\"id\":372,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-26 22:14:09\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"轮播图0727-1\",\"type\":3,\"typeCode\":\"LBT\",\"typeName\":\"轮播图\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-26 00:00:00\",\"contentId\":31,\"createTime\":\"2017-07-26 07:17:35\",\"endTime\":\"2017-09-26 00:00:00\",\"id\":371,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-26 22:11:41\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"轮播图 0726-2\",\"type\":3,\"typeCode\":\"LBT\",\"typeName\":\"轮播图\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-24 00:00:00\",\"contentId\":39,\"createTime\":\"2017-07-26 06:13:44\",\"endTime\":\"2017-12-30 23:00:00\",\"id\":370,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-26 06:14:08\",\"reviewUser\":\"worker_7\",\"status\":0,\"supportTerminal\":0,\"title\":\"弹窗广告1\",\"type\":7,\"typeCode\":\"TCGG\",\"typeName\":\"弹窗广告\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-24 00:00:00\",\"contentId\":38,\"createTime\":\"2017-07-26 06:10:16\",\"endTime\":\"2017-12-29 23:00:00\",\"id\":369,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":0,\"reviewTime\":\"\",\"reviewUser\":\"\",\"status\":1,\"supportTerminal\":0,\"title\":\"弹窗广告1\",\"type\":7,\"typeCode\":\"TCGG\",\"typeName\":\"弹窗广告\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-26 00:00:00\",\"contentId\":30,\"createTime\":\"2017-07-26 05:42:35\",\"endTime\":\"2017-08-23 00:00:00\",\"id\":368,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-26 07:18:40\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"轮播图0726-1\",\"type\":3,\"typeCode\":\"LBT\",\"typeName\":\"轮播图\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-25 00:00:00\",\"contentId\":208,\"createTime\":\"2017-07-26 02:24:59\",\"endTime\":\"2017-07-25 00:00:00\",\"id\":367,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":0,\"reviewTime\":\"\",\"reviewUser\":\"\",\"status\":1,\"supportTerminal\":0,\"title\":\"ddd\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-25 00:00:00\",\"contentId\":207,\"createTime\":\"2017-07-26 02:24:26\",\"endTime\":\"2017-07-25 00:00:00\",\"id\":366,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":0,\"reviewTime\":\"\",\"reviewUser\":\"\",\"status\":1,\"supportTerminal\":0,\"title\":\"ddd\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-23 00:00:00\",\"contentId\":206,\"createTime\":\"2017-07-26 02:23:50\",\"endTime\":\"2017-07-31 00:00:00\",\"id\":365,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":0,\"reviewTime\":\"\",\"reviewUser\":\"\",\"status\":1,\"supportTerminal\":0,\"title\":\"优惠二\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-26 00:00:00\",\"contentId\":205,\"createTime\":\"2017-07-26 02:11:25\",\"endTime\":\"2017-07-27 00:00:00\",\"id\":363,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":0,\"reviewTime\":\"\",\"reviewUser\":\"\",\"status\":1,\"supportTerminal\":0,\"title\":\"123\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-26 00:00:00\",\"contentId\":204,\"createTime\":\"2017-07-26 02:05:05\",\"endTime\":\"2017-07-27 00:00:00\",\"id\":362,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-27 09:05:06\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"Active072601\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-26 00:00:00\",\"contentId\":203,\"createTime\":\"2017-07-26 02:01:32\",\"endTime\":\"2017-08-05 00:00:00\",\"id\":361,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-26 02:01:55\",\"reviewUser\":\"worker_7\",\"status\":0,\"supportTerminal\":0,\"title\":\"0726优惠测试\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-23 00:00:00\",\"contentId\":202,\"createTime\":\"2017-07-25 23:54:32\",\"endTime\":\"2017-07-31 00:00:00\",\"id\":353,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-25 23:55:07\",\"reviewUser\":\"worker_7\",\"status\":0,\"supportTerminal\":0,\"title\":\"优惠二\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-23 00:00:00\",\"contentId\":201,\"createTime\":\"2017-07-25 23:51:56\",\"endTime\":\"2017-07-31 00:00:00\",\"id\":352,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-25 23:52:16\",\"reviewUser\":\"worker_7\",\"status\":0,\"supportTerminal\":0,\"title\":\"优惠一\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-25 00:00:00\",\"contentId\":200,\"createTime\":\"2017-07-25 09:05:29\",\"endTime\":\"2017-08-04 00:00:00\",\"id\":349,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-25 08:33:31\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"ddd\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-24 00:00:00\",\"contentId\":199,\"createTime\":\"2017-07-25 08:58:13\",\"endTime\":\"2017-07-25 00:00:00\",\"id\":348,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-25 23:52:20\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"中国\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-25 00:00:00\",\"contentId\":198,\"createTime\":\"2017-07-25 08:33:25\",\"endTime\":\"2017-08-05 00:00:00\",\"id\":347,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-25 08:33:31\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"各种优惠来袭\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-25 00:00:00\",\"contentId\":197,\"createTime\":\"2017-07-25 08:29:56\",\"endTime\":\"2017-08-05 00:00:00\",\"id\":346,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-25 08:30:03\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"test090\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-25 00:00:00\",\"contentId\":196,\"createTime\":\"2017-07-25 08:28:06\",\"endTime\":\"2017-08-05 00:00:00\",\"id\":345,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-25 08:28:12\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"各种优惠活动\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-25 00:00:00\",\"contentId\":195,\"createTime\":\"2017-07-25 08:12:33\",\"endTime\":\"2017-08-04 00:00:00\",\"id\":344,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-25 08:12:50\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"最新的优惠，快来参加\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-25 00:00:00\",\"contentId\":194,\"createTime\":\"2017-07-25 02:49:33\",\"endTime\":\"2017-08-26 00:00:00\",\"id\":341,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-25 02:49:48\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"072502test\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-25 00:00:00\",\"contentId\":193,\"createTime\":\"2017-07-25 02:31:40\",\"endTime\":\"2017-08-05 00:00:00\",\"id\":340,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-25 02:32:36\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"072501优惠\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-12 00:00:00\",\"contentId\":192,\"createTime\":\"2017-07-24 23:57:07\",\"endTime\":\"2017-07-29 00:00:00\",\"id\":339,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-25 02:32:38\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"新优惠\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-24 00:00:00\",\"contentId\":191,\"createTime\":\"2017-07-24 21:44:10\",\"endTime\":\"2017-07-24 00:00:00\",\"id\":338,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-24 21:44:23\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"刚发的\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-23 00:00:00\",\"contentId\":190,\"createTime\":\"2017-07-24 05:11:19\",\"endTime\":\"2017-07-29 00:00:00\",\"id\":337,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-24 21:44:24\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"规范哈哈哈\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-24 00:00:00\",\"contentId\":189,\"createTime\":\"2017-07-24 05:08:48\",\"endTime\":\"2017-07-27 00:00:00\",\"id\":336,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-24 05:09:45\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"过分过分\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-23 00:00:00\",\"contentId\":188,\"createTime\":\"2017-07-24 05:08:20\",\"endTime\":\"2017-07-29 00:00:00\",\"id\":335,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-24 05:09:45\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"佛丰东股份\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-17 00:00:00\",\"contentId\":187,\"createTime\":\"2017-07-24 05:07:50\",\"endTime\":\"2017-07-29 00:00:00\",\"id\":334,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-24 05:09:44\",\"reviewUser\":\"worker_7\",\"status\":0,\"supportTerminal\":0,\"title\":\"重复的收费\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-23 00:00:00\",\"contentId\":186,\"createTime\":\"2017-07-24 05:07:05\",\"endTime\":\"2017-07-28 00:00:00\",\"id\":333,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-24 05:09:42\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"第三方\",\"type\":2,\"typeCode\":\"YHHD\",\"typeName\":\"优惠活动\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-24 00:00:00\",\"contentId\":37,\"createTime\":\"2017-07-24 02:00:52\",\"endTime\":\"2017-07-31 00:00:00\",\"id\":332,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-24 02:01:13\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"弹窗广告1\",\"type\":7,\"typeCode\":\"TCGG\",\"typeName\":\"弹窗广告\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-23 00:00:00\",\"contentId\":36,\"createTime\":\"2017-07-24 02:00:13\",\"endTime\":\"2017-07-31 00:00:00\",\"id\":331,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-24 02:00:20\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"广告2\",\"type\":7,\"typeCode\":\"TCGG\",\"typeName\":\"弹窗广告\",\"uploadUser\":\"owner2_dl\"},{\"beginTime\":\"2017-07-24 00:00:00\",\"contentId\":35,\"createTime\":\"2017-07-24 01:59:00\",\"endTime\":\"2017-07-31 00:00:00\",\"id\":330,\"ownerId\":10001,\"refuseReason\":\"\",\"reviewStatus\":1,\"reviewTime\":\"2017-07-24 02:00:21\",\"reviewUser\":\"worker_7\",\"status\":2,\"supportTerminal\":0,\"title\":\"弹窗广告1\",\"type\":7,\"typeCode\":\"TCGG\",\"typeName\":\"弹窗广告\",\"uploadUser\":\"owner2_dl\"}],\"page\":1,\"total\":16}}";


        Object object = JSON.parse(data);
        if (object instanceof Map) {
            walkMap((Map<String, Object>) object);
        } else if (object instanceof List) {
            walkList((List) object);
        }
        System.out.println(JSON.toJSONString(object));
    }

}
