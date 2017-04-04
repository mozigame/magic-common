package com.magic.api.commons.tools;

import com.magic.api.commons.ApiLogger;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by weixc on 16/6/27.
 */
public class ContentUtil {
    /**
     * 客户端文案返回工具
     * @param startTime
     * @param endTime
     * @return
     */
    public static String clientContent(long startTime, long endTime) {
        String appointTime = null;
        if (startTime > 0 && endTime >0 ) {
            String startDeliveryTime = CommonDateParseUtil.format(startTime, "yyyy-MM-dd HH:mm");
            String endDeliveryTime = CommonDateParseUtil.format(endTime, "yyyy-MM-dd HH:mm");
            if (StringUtils.isNotBlank(endDeliveryTime) && endDeliveryTime.contains(" ")) {
                appointTime = startDeliveryTime + "-" + endDeliveryTime.split(" ")[1];
            }else {
                ApiLogger.error("Monitor:ContentUtil.clientContent | 组装日期异常 格式化后 startTime:"+startDeliveryTime+" endTime:"+endDeliveryTime);
            }
        }else if(startTime == -2 && endTime == -2) {
            appointTime = "到货即送(16:00-19:00)";
        }
        return appointTime;
    }

    /**
     * protal后台文案返回工具
     * @param startTime
     * @param endTime
     * @return
     */
    public static String backStageContent(long startTime, long endTime) {
        String appointTime = null;
        if(startTime > 0 && endTime > 0) {
            String startTimeString = CommonDateParseUtil.format(startTime,CommonDateParseUtil.YYYY_MM_DD_HH_MM_SS);
            String endTimeString = CommonDateParseUtil.format(endTime,CommonDateParseUtil.YYYY_MM_DD_HH_MM_SS);
            if(StringUtils.isNotEmpty(startTimeString) && StringUtils.isNotEmpty(endTimeString)) {
                appointTime = startTimeString+"/"+endTimeString;
            }else {
                ApiLogger.error("Monitor:ContentUtil.clientContent | 组装日期异常 格式化后 startTime:"+startTimeString+" endTime:"+endTimeString);
            }
        }else if (startTime == -2 && endTime == -2) {
            appointTime = "到货即送(16:00-19:00)";
        }
        return appointTime;
    }
}
