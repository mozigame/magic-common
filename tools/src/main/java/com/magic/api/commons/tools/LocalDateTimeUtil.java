package com.magic.api.commons.tools;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTimeUtil
 *
 * @author zj
 * @date 2017/7/4
 */
public class LocalDateTimeUtil {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYYMMDD = "yyyyMMdd";

    /**
     * 美东时间
     *
     * @param timestamp
     * @return
     */
    public static String toAmerica(long timestamp){
        return toAmerica(timestamp, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 美东时间
     *
     * @param timestamp
     * @param format
     * @return
     */
    public static String toAmerica(long timestamp, String format){
        LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("America/New_York"));
        return now.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * 转换美东时间的LocalDateTime
     *
     * @param timestamp

























     * @return
     */
    public static LocalDateTime toLocalDateTime(long timestamp){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("America/New_York"));
    }

    /**
     * 转换美东时间的LocalDate
     *
     * @param timestamp
     * @return
     */
    public static LocalDate toLocalDate(long timestamp){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("America/New_York")).toLocalDate();
    }

    /**
     * 转换美东时间的LocalTime
     *
     * @param timestamp
     * @return
     */
    public static LocalTime toLocalTime(long timestamp){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("America/New_York")).toLocalTime();
    }
}
