package com.magic.api.commons.tools;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ClassName:
 * Description:
 * Company:
 * auther:andy
 * date:2017/4/25
 */
public class DateUtil extends DateUtils {
    public static final String formatDefaultTimestamp = "yyyy-MM-dd HH:mm:ss";
    public static final String format_yyyy_MM_dd_HHmm = "yyyy-MM-dd HH:mm";
    public static final String format_yyyyMMddHHmm = "yyyyMMddHHmm";
    public static final String format_yyyyMMddHH = "yyyyMMddHH";
    public static final String format_yyyyMMdd = "yyyyMMdd";
    public static final String format_yyyyMM = "yyyyMM";
    public static final String format_yyyy_MM_dd = "yyyy-MM-dd";
    public static final String format_yyyyMMddHHmmss = "yyyyMMddHHmmss";

    public static final String[] weekStr = {"sun", "mon", "tue", "wed", "thu", "fri", "sat"};

    public enum WEEK {
        MON("MON", 2),
        TUE("TUE", 3),
        WED("WED", 4),
        THU("THU", 5),
        FRI("FRI", 6),
        SAT("SAT", 7),
        SUN("SUN", 1);

        private String name;
        private int index;

        private WEEK(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public static int getIndexByName(String name) {
            for (WEEK w : WEEK.values()) {
                if (w.getName().equals(name)) {
                    return w.getIndex();
                }
            }
            return 0;
        }
    }

    /**
     * 功能：判断字符串是否为日期格式
     *
     * @param strDate
     * @return
     */
    public static boolean isDate(String strDate) {
        Pattern pattern = Pattern
                .compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(strDate);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将相应格式的时间字符串转成DATE
     *
     * @param date
     * @return
     */
    public static Date parseDate(String date, String formatType) {
        SimpleDateFormat f = new SimpleDateFormat(formatType);
        Date innerDate;
        try {
            innerDate = f.parse(date);
        } catch (ParseException e) {
            innerDate = new Date();
            e.printStackTrace();
        }
        return innerDate;
    }

    /**
     * 获取相应格式的当前时间
     * 不传参数，使用默认格式
     * @param formatType
     * @return
     */
    public static String getCurrentFormatDate(String formatType) {
        if (StringUtils.isEmpty(formatType)) {
            formatType = formatDefaultTimestamp;
        }
        Locale locale = Locale.US; //美国时间
        SimpleDateFormat dateStyle = new SimpleDateFormat(formatType, locale);
        return dateStyle.format(new Date());
    }

    /**
     * 把日期时间格式化为指定格式，如：yyyy-MM-dd HH:mm
     *
     * @param dt         java.util.Date
     * @param formatType : 指定日期转化格式字符串模板,例如:yyyy-MM-dd
     * @return 格式化后的日期时间字符串
     */
    public static String formatDateTime(Date dt, String formatType) {
        String newDate = "";
        if (dt != null) {
            Locale locale = Locale.CHINESE;
            SimpleDateFormat dateStyle = new SimpleDateFormat(formatType, locale);
            newDate = dateStyle.format(dt);
        }
        return newDate;
    }

    /**
     * 将时间格式字符串转换为时间对象
     *
     * @param strDate
     * @return
     */
    public static Date format(String strDate, String aFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(aFormat);
        ParsePosition pos = new ParsePosition(0);
        return formatter.parse(strDate, pos);
    }

    /**
     * 获取某一天是星期几
     *
     * @param date
     * @return
     * @author
     * @date 2013-5-10
     */
    public static int getWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 获取某一天的前一天
     *
     * @param date
     * @return
     * @author
     * @date 2013-6-7
     */
    public static Date getYesterday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DATE, -1);
        return cal.getTime();
    }

    /**
     * 根据一个时间戳(长整形字符串)生成指定格式时间字符串
     *
     * @param time   时间戳(长整形字符串)
     * @param format 格式字符串如yyyy-MM-dd
     * @return 时间字符串
     */
    public static String getDate(long time, String format) {
        Date d = new Date();
        d.setTime(time);
        DateFormat df = new SimpleDateFormat(format);
        return df.format(d);
    }

    public static Date getDate(long time) {
        Date d = new Date();
        d.setTime(time);

        return d;
    }

    /**
     * 秒时间戳，转日期时间戳
     *
     * @param timeStamp
     * @param format
     * @return
     */
    public static long getTimeStampDate(String timeStamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(Long.parseLong(timeStamp + "000")));
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = df.parse(sd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long s = date.getTime() / 1000;
        return s;
    }

    /**
     * 时间戳转日期
     *
     * @param timeStamp
     * @return
     */
    public static String getDateTimeStamp(String timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(Long.parseLong(timeStamp + "000")));
        return sd;
    }

    /**
     * 返回一个指定数字后的时间
     *
     * @param x 指定几分钟
     * @return
     */
    public static String getTimeMinuteAdd(Date date, int x) {
        long new_d = date.getTime() + (x * 60 * 1000);
        return getDate(new_d, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 当前时间+Interval 分钟 后的时间
     *
     * @param Interval
     * @return
     * @author
     * @date 2013-6-6
     */
    public static Date addDate(int Interval) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); // 设置当前日期
        c.add(Calendar.MINUTE, Interval); // 日期分钟加1,Calendar.DATE(天),Calendar.HOUR(小时)
        Date date = c.getTime();
        return date;

    }

    public static String getFutureDay(String appDate, String format, int days) {
        String future = "";
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            Date date = simpleDateFormat.parse(appDate);
            calendar.setTime(date);
            calendar.add(Calendar.DATE, days);
            date = calendar.getTime();
            future = simpleDateFormat.format(date);
        } catch (Exception e) {

        }

        return future;
    }

    /**
     * 字符型时间变成时间类型
     *
     * @param date   字符型时间 例如: "2008-08-08"
     * @param format 格式化形式 例如: "yyyy-MM-dd"
     * @return 出现异常时返回null
     */
    public static Date getFormatDate(String date, String format) {
        DateFormat df = new SimpleDateFormat(format);
        Date d = null;
        if (date == null) {
            return d;
        }
        try {
            d = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    /**
     * 得到今天的星期
     * 美国时间
     * @return 今天的星期
     */
    public static String getWeeks(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.US);
        return sdf.format(date);
    }

    /**
     * 根据一个时间戳(长整形字符串)生成指定格式时间字符串
     *
     * @param date   时间戳(长整形字符串)
     * @param format 格式字符串如yyyy-MM-dd
     * @return 时间字符串
     */
    public static String getDate(Date date, String format) {
        String formatDate = "";
        if (date != null) {
            DateFormat df = new SimpleDateFormat(format);
            formatDate = df.format(date);
        }
        return formatDate;
    }

    /**
     * 日期转换(竞彩专用)
     *
     * @param agalistNo
     */
    public static String getDate(String agalistNo) {
        String resDate = "";// 日期和星期
        int iss = Integer.valueOf(agalistNo.substring(8, 9));
        switch (iss) {
            case 1:
                resDate = "周一" + " " + agalistNo.substring(9, 12);
                break;
            case 2:
                resDate = "周二" + " " + agalistNo.substring(9, 12);
                break;
            case 3:
                resDate = "周三" + " " + agalistNo.substring(9, 12);
                break;
            case 4:
                resDate = "周四" + " " + agalistNo.substring(9, 12);
                break;
            case 5:
                resDate = "周五" + " " + agalistNo.substring(9, 12);
                break;
            case 6:
                resDate = "周六" + " " + agalistNo.substring(9, 12);
                break;
            case 7:
                resDate = "周日" + " " + agalistNo.substring(9, 12);
                break;
            default:
                break;
        }

        return resDate;
    }

    /**
     * 求两个日期差
     *
     * @param beginDate 开始日期
     * @param endDate   结束日期
     * @return 两个日期相差天数
     */
    public static long getDateMargin(Date beginDate, Date endDate) {
        long margin = 0;
        margin = endDate.getTime() - beginDate.getTime();
        margin = margin / (1000 * 60 * 60 * 24);
        return margin;
    }


    public static Date getEndDate(int day, String type) {
        return null;
    }


    /**
     * 判断时间是否是昨天的日期
     *
     * @param time
     * @return
     */
    public static int isYesterday(String time) {
        SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (time == null || "".equals(time)) {
            return -1;
        }
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar current = Calendar.getInstance();

        Calendar today = Calendar.getInstance();    //今天

        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
        //  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar yesterday = Calendar.getInstance();    //昨天

        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
        yesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) - 1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);

        current.setTime(date);
        //昨天，返回1
        if (current.before(today) && current.after(yesterday)) {
            return 0;
        }
        if (current.after(today)) {
            //如果是今天，返回false
            return 1;
        }
        return -1;
    }

    /**
     * 格式化时间
     *
     * @param dateStr
     * @param fromFormat
     * @param toFormat
     * @return
     */
    public static String getFormatString(String dateStr, String fromFormat, String toFormat) {
        if (StringUtils.isEmpty(dateStr) && StringUtils.isEmpty(fromFormat) && StringUtils.isEmpty(toFormat)) {
            return null;
        }
        try {
            Date date = DateUtil.getFormatDate(dateStr, fromFormat);
            SimpleDateFormat format1 = new SimpleDateFormat(toFormat);
            return format1.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * start
     * 本周开始时间戳
     */
    public static long getWeekStartTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        // 获取星期日开始时间戳
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String time = simpleDateFormat.format(cal.getTime()) + "000000";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        long l = 0l;
        try {
            l = sdf.parse(time).getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return l;
    }

    /**
     * end
     * 本周结束时间戳
     */
    public static long getWeekEndTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        // 获取星期六结束时间戳
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        String time = simpleDateFormat.format(cal.getTime()) + "235959";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        long l = 0l;
        try {
            l = sdf.parse(time).getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return l;
    }


    /**
     * 获取当前时间到第二天零点的秒数
     *
     * @return
     */
    public synchronized static int getSurplusDaySecond() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long l = calendar.getTimeInMillis() - System.currentTimeMillis();
        return (int) l / 1000;
    }

    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.of("America/New_York"));
        String string = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(string);
    }
}
