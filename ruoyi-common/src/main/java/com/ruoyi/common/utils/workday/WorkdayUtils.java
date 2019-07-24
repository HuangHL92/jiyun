package com.ruoyi.common.utils.workday;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.*;
import java.util.*;

public class WorkdayUtils {
    /**
     * 预设工作日数据的开始年份
     */
    private static final int START_YEAR = 2017;

    /**
     * 预设工作日数据的结束年份
     */
    private static final int END_YEAR = 2030;

    /**
     * 起始日期处理策略
     */
    private static final BoundaryDateHandlingStrategy START_DATE_HANDLING_STRATEGY = date -> {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY) < 12; // 如果开始时间在中午12点前，则当天也算作一天，否则不算
    };

    /**
     * 结束日期处理策略
     */
    private static final BoundaryDateHandlingStrategy END_DATE_HANDLING_STRATEGY = date -> {
        return true;    // 结束时间无论几点，都算作1天
    };

    /**
     * 工作日map，true为补休，false为放假
     */
    private static Map<Integer, Boolean> WORKDAY_MAP = new HashMap<>();

    private static SegmentTree SEGMENT_TREE;

    public static void init(HashMap<Integer, Integer> map) {

        if(map!=null) {
            initWorkday(map); // 初始化工作日map
        }


        // 计算从START_YEAR到END_YEAR一共有多少天
        int totalDays = 0;
        for (int year = START_YEAR; year <= END_YEAR; ++year) {
            totalDays += getDaysOfYear(year);
        }
        int[] workdayArray = new int[totalDays];    // 将工作日的数据存入到数组
        Calendar calendar = new GregorianCalendar(START_YEAR, 0, 1);
        for (int i = 0; i < totalDays; ++i) {
            // 将日期转为yyyyMMdd格式的int
            int datestamp = calendar.get(Calendar.YEAR) * 10000 + (calendar.get(Calendar.MONTH) + 1) * 100 + calendar.get(Calendar.DAY_OF_MONTH);
            Boolean isWorkDay = WORKDAY_MAP.get(datestamp);
            if (isWorkDay != null) { // 如果在工作日map里有记录，则按此判断工作日
                workdayArray[i] = isWorkDay ? 1 : 0;
            } else { // 如果在工作日map里没记录，则按是否为周末判断工作日
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                workdayArray[i] = (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) ? 1 : 0;
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        SEGMENT_TREE = new SegmentTree(workdayArray);   // 生成线段树
    }

    /**
     * 计算两个日期之间有多少个工作日<br/>
     *
     * @param
     * @param
     * @return
     */
    public static int howManyWorkday(Date startDate, Date endDate) {

        try {
            if(SEGMENT_TREE==null) {
                init(null);
            }
            if (startDate.after(endDate)) {
                return howManyWorkday(endDate, startDate);
            }

            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startDate);
            int startDays = getDaysAfterStartYear(startCalendar) - 1;   // 第一天从0开始

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endDate);
            int endDays = getDaysAfterStartYear(endCalendar) - 1;   // 第一天从0开始

            if (startDays == endDays) { // 如果开始日期和结束日期在同一天的话
                return isWorkDay(startDate) ? 1 : 0;    // 当天为工作日则返回1天，否则0天
            }

            if (!START_DATE_HANDLING_STRATEGY.ifCountAsOneDay(startDate)) { // 根据处理策略，如果开始日期不算一天的话
                ++startDays;    // 起始日期向后移一天
            }

            if (!END_DATE_HANDLING_STRATEGY.ifCountAsOneDay(endDate)) { // 根据处理策略，如果结束日期不算一天的话
                --endDays;  // 结束日期向前移一天
            }
            return SEGMENT_TREE.query(startDays, endDays);

        }catch (Exception ex) {
            return 0;
        }

    }

    /**
     * 是否为工作日
     *
     * @param date
     * @return
     */
    public static boolean isWorkDay(Date date) {

        if(SEGMENT_TREE==null) {
            init(null);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int days = getDaysAfterStartYear(calendar) - 1;
        return SEGMENT_TREE.query(days, days) == 1;
    }

    /**
     * 计算从开始年份到这个日期有多少天
     *
     * @param calendar
     * @return
     */
    private static int getDaysAfterStartYear(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        if (year < START_YEAR || year > END_YEAR) {
            throw new IllegalArgumentException(String.format("系统目前仅支持计算%d年至%d年之间的工作日，无法计算%d年！", START_YEAR, END_YEAR, year));
        }
        int days = 0;
        for (int i = START_YEAR; i < year; ++i) {
            days += getDaysOfYear(i);
        }
        days += calendar.get(Calendar.DAY_OF_YEAR);
        return days;
    }

    /**
     * 计算该年有几天，闰年返回366，平年返回365
     *
     * @param year
     * @return
     */
    private static int getDaysOfYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0 ? 366 : 365;
    }

    /**
     * 初始化工作日Map<br/>
     * 日期格式必须为yyyyMMdd，true为补休，false为放假，如果本来就是周末的节假日则不需再设置
     */
    public static void initWorkday(HashMap<Integer, Integer> map) {
        Iterator<Map.Entry<Integer, Integer>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Integer, Integer> entry = entries.next();
            if (entry.getValue() == 3) {
                WORKDAY_MAP.put(entry.getKey(), false);
            } else if (entry.getValue() == 4) {
                WORKDAY_MAP.put(entry.getKey(), true);
            }
        }

    }

    public static Date integerToDate() {
        return new Date();
    }

    public static Integer Date2Int(Date dateStr) {
        int ts = Integer.valueOf(DateUtil.format(dateStr,"yyyyMMdd"));
        return ts;
    }

    /**
     * 边界日期处理策略<br/>
     * 在计算两个日期之间有多少个工作日时，有的特殊需求是如果开始/结束的日期在某个时间之前/后（如中午十二点前），则不把当天算作一天<br/>
     * 因此特将此逻辑分离出来，各自按照不同需求实现该接口即可
     *
     * @author Corvey
     * @Date 2018年11月12日15:38:16
     */
    private interface BoundaryDateHandlingStrategy {
        /**
         * 是否把这个日期算作一天
         */
        boolean ifCountAsOneDay(Date date);
    }

    /**
     * zkw线段树
     *
     * @author Corvey
     */
    private static class SegmentTree {

        private int[] data; // 线段树数据
        private int numOfLeaf; // 叶子结点个数

        public SegmentTree(int[] srcData) {
            for (numOfLeaf = 1; numOfLeaf < srcData.length; numOfLeaf <<= 1) ;
            data = new int[numOfLeaf << 1];
            for (int i = 0; i < srcData.length; ++i) {
                data[i + numOfLeaf] = srcData[i];
            }
            for (int i = numOfLeaf - 1; i > 0; --i) {
                data[i] = data[i << 1] + data[i << 1 | 1];
            }
        }

        /**
         * [left, right]区间求和，left从0开始
         */
        public int query(int left, int right) {
            if (left > right) {
                return query(right, left);
            }
            left = left + numOfLeaf - 1;
            right = right + numOfLeaf + 1;
            int sum = 0;
            for (; (left ^ right ^ 1) != 0; left >>= 1, right >>= 1) {
                if ((~left & 1) == 1) {
                    sum += data[left ^ 1];
                }
                if ((right & 1) == 1) {
                    sum += data[right ^ 1];
                }
            }
            return sum;
        }
    }

    /**
     * 此方法计算某个日期加上几个工作日后的一个工作日期(除周末)
     *
     * @param date(起始日期) , day(要添加的工作天数)
     * @return incomeDate(去除周末后的日期)
     */
    public static Date getIncomeDate(Date date, int days) throws NullPointerException {

        if(SEGMENT_TREE==null) {
            init(null);
        }

        Date incomeDate = date;
        for (int i = 1; i <= days; i++) {
            incomeDate = getIncomeDate(incomeDate);
        }
        incomeDate = DateUtil.parse(DateUtil.format(incomeDate,"yyyy-MM-dd"));

        return incomeDate;
    }

    /**
     * 此方法计算某个日期后一天的工作日期(除周末)
     *
     * @param date(起始日期)
     * @return incomeDate(去除周末后的日期)
     */
    private static Date getIncomeDate(Date date) throws NullPointerException {
        if (null == date) {
            throw new NullPointerException("the date is null or empty!");
        }

        //对日期的操作,我们需要使用 Calendar 对象
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        //+1天
        calendar.add(Calendar.DAY_OF_MONTH, +1);

        //判断是星期几
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        Date incomeDate = calendar.getTime();
        //判断是否为节假日
        if (!isWorkDay(incomeDate)){
            System.out.println(incomeDate);
            return getIncomeDate(incomeDate);
        }

        return incomeDate;
    }

    public static Date Integer2Date(Integer days) {

        String dayStr = days.toString();
        String date = StringUtils.format("{}-{}-{}",dayStr.substring(0,4),dayStr.substring(4,6),dayStr.substring(6,8));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date datetime = null;
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return datetime;
    }
    /**
     * String转Calendar
     *
     * @param sdate
     * @return
     */
    public static Calendar String2Calendar(String sdate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date cdate = null;
        try {
            cdate = sdf.parse(sdate);
        } catch (ParseException e) {

            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(cdate);
        return calendar;
    }

    /**
     * 计算两个时间差（小时）
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public double calHourBetweenTwoDate(Date startTime, Date endTime) {
        long nh = 1000 * 60 * 60;
        long diff = endTime.getTime() - startTime.getTime();
        double hour = (double) diff / nh;
        return hour;
    }

    /**
     * 生成一个上下班时间，因为夏季冬季不一样
     *
     * @param
     * @return
     */
    private Date chang2TodayTime(String timeStr, Date sedate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(sedate);
        String date = dateStr + " " + timeStr;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date datetime = null;
        try {
            datetime = df.parse(date);
        } catch (ParseException e) {

            e.printStackTrace();
        }
        return datetime;
    }

    /**
     * 将Date类型转换String yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String date2String(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * 判断是否是本周
     *
     * @param time
     * @return
     */
    public static boolean isThisWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(new Date(time));
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        if (paramWeek == currentWeek) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是某一天
     *
     * @param date 要比较的日期
     * @param day  哪一个天
     * @return
     */
    public static boolean isToday(Date date, Date day) {
        return isThisTime(date, day, "yyyy-MM-dd");
    }

    /**
     * 判断选择的日期是否是本月
     *
     * @param date  要比较的日期
     * @param Month 哪一个月
     * @return
     */
    public static boolean isThisMonth(Date date, Date Month) {
        return isThisTime(date, Month, "yyyy-MM");
    }

    private static boolean isThisTime(Date date, Date Month, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String param = sdf.format(date);//参数时间
        String now = sdf.format(Month);//当前时间
        if (param.equals(now)) {
            return true;
        }
        return false;
    }
}

