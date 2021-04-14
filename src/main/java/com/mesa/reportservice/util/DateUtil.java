package com.mesa.reportservice.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * <p>时间/日期处理工具类</p>
 *
 * @author 中科智源育成信息有限公司 E-mail: doufengh@software.ict.ac.cn
 * @version 1.0 创建时间：2010-11-8 下午04:48:57
 */
public final class DateUtil {

    private static SimpleDateFormat simpleDateFormat = null;
    /**
     * 处理日期时，用到参数。格式24小时制yyyy-MM-dd HH:mm:ss
     */
    private static String YYYY_MM_DD_HH24_MM_SS = "yyyy-MM-dd HH:mm:ss";
    /**
     * 处理日期时，用到参数。格式12小时制yyyy-MM-dd hh:mm:ss
     */
    private static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd hh:mm:ss";
    /**
     * 处理日期时，用到参数。格式为yyyy-MM-dd
     */
    private static String YYYY_MM_DD = "yyyy-MM-dd";
    /**
     * 处理日期时，用到参数。格式24小时制yyyy-MM-dd HH:mm
     */
    private static String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    /**
     * 处理日期时，用到参数。格式24小时制yyyyMMddHHmmss
     */
    private static String YYYYMMDDHH24MMSS = "yyyyMMddHHmmss";
    /**
     * 处理日期时，用到参数。格式为yyyyMMdd
     */
    private static String YYYYMMDD = "yyyyMMdd";
    /**
     * 处理日期时，用到参数。格式为HHmmss
     */
    private static String HH24MMSS = "HHmmss";

    /**
     * <p>Description:抑制默认的构造器,避免实例化对象 </p>
     */
    private DateUtil() {

    }


    /**
     * <p>获得当前日期，格式为默认yyyy-MM-dd.</p>
     * <pre>
     * DateUtil.getCurrentDate() = "2010-11-02"
     * </pre>
     *
     * @return String    返回标准日期格式(yyyy-MM-dd)
     * @see #getCurrentDate(String)
     */
    public static String getCurrentDate() {

        simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD);
        return simpleDateFormat.format(new Date());
    }

    /**
     * <p>
     * 获得当前时间.
     * 如果pattern为空,将抛出{@link NullPointerException}
     * </p>
     * <pre>
     * DateUtil.getCurrentDate(TimeConstants.YYYY_MM_DD) = "2010-11-02"
     * DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss") = "2010-11-02 17:20:59"
     * </pre>
     *
     * @param pattern 描述日期和时间格式.
     * @return 当前日期
     */
    public static String getCurrentDate(String pattern) {

        if (StringUtil.isEmpty(pattern)) {
            throw new NullPointerException("未规定时间格式.");
        }
        simpleDateFormat = new SimpleDateFormat(DateUtil.YYYY_MM_DD_HH_MM_SS);
        return simpleDateFormat.format(new Date());


    }

    public static String getDate() {


        simpleDateFormat = new SimpleDateFormat(DateUtil.YYYY_MM_DD_HH24_MM_SS);
        return simpleDateFormat.format(new Date());


    }

    public static String getBeforeDate(int time) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dt = df.format(new Date());
        String[] s = dt.split(":");
        Integer i = Integer.valueOf(s[1]);
        String ret = "";
        switch (time) {
            case 300:
                if (i < 5) {
                    ret = s[0] + ":00:00";
                } else if (i < 10) {
                    ret = s[0] + ":05:00";
                } else if (i < 15) {
                    ret = s[0] + ":10:00";
                } else if (i < 20) {
                    ret = s[0] + ":15:00";
                } else if (i < 25) {
                    ret = s[0] + ":20:00";
                } else if (i < 30) {
                    ret = s[0] + ":25:00";
                } else if (i < 35) {
                    ret = s[0] + ":30:00";
                } else if (i < 40) {
                    ret = s[0] + ":35:00";
                } else if (i < 45) {
                    ret = s[0] + ":40:00";
                } else if (i < 50) {
                    ret = s[0] + ":45:00";
                } else if (i < 55) {
                    ret = s[0] + ":50:00";
                } else if (i <= 59) {
                    ret = s[0] + ":55:00";
                }
                break;
            case 900:
                if (i < 15) {
                    ret = s[0] + ":00:00";
                } else if (i < 30) {
                    ret = s[0] + ":15:00";
                } else if (i < 45) {
                    ret = s[0] + ":30:00";
                } else if (i < 60) {
                    ret = s[0] + ":45:00";
                }
                break;
            case 60:
                /*Long l = new Date().getTime();
                Long w = l - 60000;
                SimpleDateFormat dd = new SimpleDateFormat(DateUtil.YYYY_MM_DD_HH_MM);
                ret = dd.format(w);
                break;*/
                if (i < 1) {
                    ret = s[0] + ":00:00";

                } else if (i >= 59) {
                    ret = s[0] + ":59:00";
                } else {
                    ret = s[0] + ":" + i + ":00";
                }
                break;

            default:
                simpleDateFormat = new SimpleDateFormat(DateUtil.YYYY_MM_DD_HH_MM);
                ret = simpleDateFormat.format(new Date());
                break;

        }

        return ret;

    }


    /**
     * <p> 获得昨天时间.通过指定日期和时间格式,具体可参考本类提供的时间格式常量.</p>
     * <pre>
     * DateUtil.getDateOfYesterday(TimeConstants.YYYY_MM_DD_HH24_MM_SS) = "2010-11-04 16:59:13"
     * </pre>
     *
     * @param pattern 描述日期和时间格式
     * @return String    返回日期的字符串表现形式值.
     * @see #getFormatDate(Date, String)
     */
    public static String getDateOfYesterday(String pattern) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(calendar.DAY_OF_MONTH, -1);
        Date time = calendar.getTime();
        return getFormatDate(time, pattern);

    }

    /**
     * <p>
     * 得到距离当天<code>offset</code>天数的某天日期，通过 <code>offset</code> 的值（+/-）来确定增减计算.
     * </p>
     * <pre>
     * 当天日期： 2010-11-05
     * DateUtil.getSomeDate("yyyy-MM-dd", 2) = 2010-11-07
     * DateUtil.getSomeDate("yyyy-MM-dd", -2) = 2010-11-03
     * DateUtil.getSomeDate("yyyy-MM-dd", 0) = 2010-11-05
     * </pre>
     *
     * @param pattern 描述日期和时间格式
     * @param offset  描述增加\减少几日,为0时为当前日期.
     * @return 返回日期格式的字符串
     * @see #getFormatDate(Date, String)
     */

    public static String getSomeDate(String pattern, int offset) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, offset);
        Date time = calendar.getTime();

        return getFormatDate(time, pattern);

    }

    /**
     * <p>获得指定日期当月第一天</p>
     * <pre>
     * DateUtil.getFirstDayOfMonth(TimeConstants.YYYY_MM_DD_HH24_MM_SS, new Date()) = "2010-11-01 00:00:00"
     * </pre>
     *
     * @param date    指定日期.
     * @param pattern 描述日期和时间格式
     * @return 返回日期格式的字符串
     */
    public static String getFirstDayOfMonth(Date date,
                                            String pattern) {

        if (StringUtil.isEmpty(pattern) || StringUtil.isEmpty(date)) {
            throw new NullPointerException();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Integer minDateNum = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);

        calendar.set(Calendar.DAY_OF_MONTH, minDateNum);
        calendar.set(Calendar.HOUR, calendar.getActualMinimum(Calendar.HOUR));
        calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.set(Calendar.MINUTE, calendar
                .getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar
                .getActualMinimum(Calendar.SECOND));

        return getFormatDate(calendar.getTime(), pattern);
    }

    /**
     * <p>获得指定日期当月最后一天</p>
     * <pre>
     * DateUtil.getLastDayOfMonth(TimeConstants.YYYY_MM_DD_HH24_MM_SS, new Date()) = "2010-11-30 23:59:59"
     * </pre>
     *
     * @param date    指定日期
     * @param pattern 描述日期和时间格式
     * @return 返回日期格式的字符串
     */
    public static String getLastDayOfMonth(Date date,
                                           String pattern) {

        if (StringUtil.isEmpty(pattern) || StringUtil.isEmpty(date)) {
            throw new NullPointerException();
        }

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        Integer maxDateNum = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH, maxDateNum);
        c.set(Calendar.HOUR, c.getActualMaximum(Calendar.HOUR));
        c.set(Calendar.AM_PM, Calendar.PM);
        c.set(Calendar.MINUTE, c.getActualMaximum(Calendar.MINUTE));
        c.set(Calendar.SECOND, c.getActualMaximum(Calendar.SECOND));
        return getFormatDate(c.getTime(), pattern);
    }

    /**
     * <p>获得指定日期的日.</p>
     *
     * @param date 日期值
     * @return 返回日期的日.如果{@link java.util.Date}为<code>null</code>,抛出异常.
     * @throws NullPointerException
     */
    public static String getDDFromDate(Date date) {
        if (null == date) {
            throw new NullPointerException("未存储日期对象");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.DATE) + "";

    }

    /**
     * <p>获得指定日期的月.</p>
     *
     * @param date 指定日期
     * @return 返回日期的月.如果{@link java.util.Date}为<code>null</code>,抛出异常.
     * @throws NullPointerException
     */
    public static String getMMFromDate(Date date) {

        if (null == date) {
            throw new NullPointerException();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);

        return calendar.get(Calendar.MONTH) + "";

    }


    /**
     * <p>获得指定日期的年.</p>
     *
     * @param date 指定日期
     * @return 返回日期的年.如果{@link java.util.Date}为<code>null</code>,抛出异常.
     * @throws NullPointerException
     */
    public static String getYYFromDate(Date date) {

        if (null == date) {
            throw new NullPointerException();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.YEAR) + "";

    }


    /**
     * <p>返回两个日期之间的所有天数,放入集合{@link List}中.</p>
     * <pre>
     * DateUtil.getDateList(DateUtil.convertStringToDate(DateUtil.DATE_PATTERN_YYYY_MM_DD,
     * 				"2010-10-22"),new Date()) =  list
     * </pre>
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 返回两个日期相隔时间集合
     * @throws NullPointerException
     */
    public static List<Date> getDateList(Date begin, Date end) {

        if (null == begin || null == end) {
            throw new NullPointerException();
        }

        List<Date> result = new ArrayList<Date>();
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(begin);
        calendar2.setTime(end);
        while (calendar1.before(calendar2)) {
            result.add(calendar1.getTime());
            calendar1.add(calendar1.DAY_OF_MONTH, 1);
        }

        return result;

    }

    /**
     * <p>根据两个日期获得之间所有的工作日期返回一个日期数组 (工作日不包括星期六和星期日)</p>
     * <pre>
     * DateUtil.getWorkingdayList(DateUtil.convertStringToDate(TimeConstants.YYYY_MM_DD,
     * "2010-10-15"),new Date()) = list
     * </pre>
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 返回两个日期相隔时间集合
     * @throws NullPointerException
     */
    public static List<Date> getWorkingdayList(Date begin,
                                               Date end) {
        if (null == begin || null == end) {
            throw new NullPointerException();
        }
        List<Date> list = getDateList(begin, end);
        List<Date> result = new ArrayList<Date>();
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < list.size(); i++) {

            calendar.setTime(list.get(i));
            if (calendar.get(Calendar.DAY_OF_WEEK) == 1
                    || calendar.get(Calendar.DAY_OF_WEEK) == 7) {
                continue;
            } else {
                result.add(list.get(i));
            }

        }
        return result;
    }

    /**
     * <p>根据日期类型的数组获得数组内所有日期对应的星期X列表</p>
     *
     * @param list 日期类型的数组
     * @return 星期X列表
     */
    public static List<String> getListOfDayOfWeek(List<Date> list) {

        List<String> result = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        for (Date date : list) {
            calendar.setTime(date);
            switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                case 1:
                    result.add("星期天");
                    break;
                case 2:
                    result.add("星期一");
                    break;
                case 3:
                    result.add("星期二");
                    break;
                case 4:
                    result.add("星期三");
                    break;
                case 5:
                    result.add("星期四");
                    break;
                case 6:
                    result.add("星期五");
                    break;
                case 7:
                    result.add("星期六");
                    break;
                default:
                    break;
            }

        }
        return result;
    }






    /**
     * <p>将10进制,转换日期字符串类型.</p>
     * <pre>
     * DateUtil.convertTimestampToString(TimeConstants.YYYY_MM_DD, 1286726400l) = "2010-10-11 00:00:00"
     * </pre>
     *
     * @param time10  时间戳值
     * @param pattern 描述日期与格式
     * @return 返回日期字符串
     * @throws NullPointerException
     * @see #getFormatDate(Date, String)
     * @see #convertStringToTimestamp(String, String)
     */
    public static String convertTimestampToString(
            long time10, String pattern) {

        if (StringUtil.isEmpty(pattern)) {
            throw new NullPointerException("参数为NULL");
        }
        Date date = new Date();
        date.setTime(time10 * 1000);
        return getFormatDate(date, pattern);
    }

    /**
     * <p>
     * 将日期转变为字符串的表现形式.
     * </p>
     * <p>
     * 日期格式可以参考本类提供的日期常量,也可自己定义日期格式.
     * 当<code>pattern</code> 或 <code>date</code> 为空时,抛出空指针异常.
     * </p>
     * <pre>
     * DateUtil.getFormatDate("yyyyMMddHHmmss", new Date()) = "20101102174448"
     * </pre>
     *
     * @param date    解析的日期值
     * @param pattern 描述日期与时间格式
     * @return 返回日期的字符串表现形式.
     * @throws NullPointerException
     */
    public static String getFormatDate(Date date, String pattern) {

        if (StringUtil.isEmpty(pattern) || StringUtil.isEmpty(date)) {
            throw new NullPointerException("参数为NULL");
        }
        simpleDateFormat = new SimpleDateFormat(pattern);

        return simpleDateFormat.format(date);
    }





}
