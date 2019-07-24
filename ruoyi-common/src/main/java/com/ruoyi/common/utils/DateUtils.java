package com.ruoyi.common.utils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 时间工具类
 *
 * @author ruoyi
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }


    public double calLeaveDays(Date startTime, Date endTime) {
        double leaveDays = 0;
        //从startTime开始循环，若该日期不是节假日或者不是周六日则请假天数+1
        Date flag = startTime;//设置循环开始日期
        Calendar cal = Calendar.getInstance();
        //循环遍历每个日期
        while (flag.compareTo(endTime) != 1) {
            cal.setTime(flag);
            //判断是否为周六日
            int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (week == 0 || week == 6) {//0为周日，6为周六
                //跳出循环进入下一个日期
                cal.add(Calendar.DAY_OF_MONTH, +1);
                flag = cal.getTime();
                continue;
            }
            //判断是否为节假日
            try {
                //从数据库查找该日期是否在节假日中
                /*这里为数据库操作*/
                /*传入该日期flag,使用sql语句判断flag是否between节假日开始日期and节假日结束日期*/
                /*count为从数据库查出的行数*/
                //TODO
                int count=2;
                if (count > 0) {
                    //跳出循环进入下一个日期
                    cal.add(Calendar.DAY_OF_MONTH, +1);
                    flag = cal.getTime();
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //不是节假日或者周末，天数+1
            leaveDays = leaveDays + 1;
            //日期往后加一天
            cal.add(Calendar.DAY_OF_MONTH, +1);
        }
        return leaveDays;
    }


    /**
     * 返回当前的LocalDateTime
     * @author zifangsky
     * @date 2018/7/30 13:23
     * @since 1.0.0
     * @return java.time.LocalDateTime
     */
    public static LocalDateTime now(){
        return LocalDateTime.now();
    }


    /**
     * 将时间戳转换为LocalDateTime
     * @author zifangsky
     * @date 2018/7/30 13:23
     * @since 1.0.0
     * @param second Long类型的时间戳
     * @param zoneOffset 时区，不填默认为+8
     * @return java.time.LocalDateTime
     */
    public static LocalDateTime ofEpochSecond(Long second, ZoneOffset zoneOffset){
        if(zoneOffset == null){
            return LocalDateTime.ofEpochSecond(second,0,ZoneOffset.ofHours(8));
        }else{
            return LocalDateTime.ofEpochSecond(second,0,zoneOffset);
        }
    }


    /**
     * 返回几天之后的时间（精确到秒的时间戳）
     * @param days
     * @param zoneOffset
     * @return
     */
    public static Long nextDaysSecond(Long days, ZoneOffset zoneOffset){
        LocalDateTime dateTime = nextDays(days);

        if(zoneOffset == null){
            return dateTime.toEpochSecond(ZoneOffset.ofHours(8));
        }else{
            return dateTime.toEpochSecond(zoneOffset);
        }
    }

    /**
     * 返回当前精确到毫秒的时间戳
     * @return
     */
    public static Long currentTimeMillis(){
        return System.currentTimeMillis();
    }

    /**
     * 返回几天之后的时间
     * @param days
     * @return
     */
    public static LocalDateTime nextDays(Long days){
        return now().plusDays(days);
    }

    /**
     * 将天数转化为秒数
     * @author zifangsky
     * @date 2018/8/18 17:45
     * @since 1.0.0
     * @param days 天数
     * @return java.lang.Integer
     */
    public static Long dayToSecond(Long days){
        return days * 86400;
    }
}
