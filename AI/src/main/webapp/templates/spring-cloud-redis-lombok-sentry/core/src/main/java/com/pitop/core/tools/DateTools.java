package ${basePackage}.core.tools;

import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateTools {
    //时间字符串 年月日时分秒
    public static SimpleDateFormat yyyyMMDDHHmmssFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //时间字符串 年月日时分
    public static SimpleDateFormat yyyyMMDDHHmmFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    //时间字符串 年月日
    public static SimpleDateFormat yyyyMMDDFmt = new SimpleDateFormat("yyyy-MM-dd");

    //时间字符串 时分秒
    public static SimpleDateFormat HHmmssFmt = new SimpleDateFormat("HH:mm:ss");

    //时间字符串 时分
    public static SimpleDateFormat HHmmFmt = new SimpleDateFormat("HH:mm");

    //格式化成统一utc时间格式
    public static SimpleDateFormat utcFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");


    //星期
    private static String[] weekDay = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};


    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd"; // 默认的日期格式
    public static final DateFormat[] ACCEPT_DATE_FORMATS = { // 定义可被转换成日期对象的字符串格式
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
            new SimpleDateFormat("yyyyMMddHHmmss"),
            new SimpleDateFormat("yyyy-MM-dd"),
            new SimpleDateFormat("yyyy-MM-dd HHmmss"),
            new SimpleDateFormat("yyyyMMdd"), new SimpleDateFormat("yyyyMM"),
            new SimpleDateFormat(DEFAULT_DATE_FORMAT),
            new SimpleDateFormat("yyyy/MM/dd"),
            new SimpleDateFormat("yyyy/MM/dd HHmmss"),
            new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"),
            new SimpleDateFormat("yyyy-MM"), new SimpleDateFormat("yyyy")
    };

    /**
     * 测试的main方法.
     *
     * @param argc
     */
    public static void main(String[] argc) throws ParseException {

        System.out.println(TimeZone.getDefault().getID());
        String[] ids = fecthAllTimeZoneIds();
        String nowDateTime = date2String("yyyy-MM-dd HH:mm:ss");
        System.out.println("The time Asia/Shanhai is " + nowDateTime);//程序本地运行所在时区为[Asia/Shanhai]
        //显示世界每个时区当前的实际时间
        for (int i = 0; i < ids.length; i++) {
            System.out.println(" * " + ids[i] + "   =   " + date2Timezone(nowDateTime, ids[i]));
        }

        System.out.println(date2Timezone("2017-08-09 17:17:00", "Asia/Tokyo"));
        System.out.println(date2Timezone("2017-08-09 17:17:00", "Europe/Paris"));
        System.out.println(date2Timezone("2017-08-11 18:12:37", "America/Los_Angeles"));
        System.out.println(date2Timezone(DateTools.format(new Date(), "yyyy-MM-dd HH:mm:ss"), "Asia/Tokyo"));

        //本地时间转化成 utc时间，utc转化成美国时间
        String date = DateTools.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        System.out.println("本地时间： " + date);
        String utcDate = date2Timezone(date, "UTC");
        System.out.println("utc:  " + utcDate);
        System.out.println("us:  " + utc2TimeZone(utcDate, "America/Los_Angeles"));
        System.out.println(toUtcFmt(new Date()));
        System.out.println(utcToDate(toUtcFmt(new Date())));


        System.out.println(utc2TimeZone("2017-08-17 06:18:16", "Asia/Shanghai"));

        System.out.println(calculateByMinute(new Date(), -12 * 60));

        System.out.println(yyyyMMddHHmmss(stringToDate("2017-08-19 00:00:00")));

        System.out.println(diffminutes("2017-08-19 00:00:00", "2017-08-19 02:00:00"));
        System.out.println(DateTools.calculateByMinute(stringToDate("2017-08-19 00:00:00"), 60));

        GregorianCalendar g = new GregorianCalendar();
        g.setTime(stringToDate("2017-08-19 00:00:00"));
//        g.setGregorianChange(stringToDate("2017-08-19 00:00:00"));
        System.out.println(g.getTime());
        g.add(GregorianCalendar.MINUTE, 60);
        System.out.println(g.getTime());

        System.out.println(getAM_PM(new Date()));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
        Date date1 = simpleDateFormat.parse("2017-09-20 18:00:00");
        System.out.println(toUtc(date1));
        Date utcDate1 = toUtc("2017-09-20 18:00:00", "Asia/Tokyo");
        System.out.println(utcDate1);
        String dstr = DateTools.format(utcDate1, "yyyy-MM-dd HH:mm:ss");
        System.out.println(dstr);
        System.out.println(utc2TimeZone("2017-09-21 09:00:00", "Asia/Tokyo"));

        System.out.println(isBefore(DateTools.stringToDate("2017-09-01 00:00:00"), DateTools.stringToDate("2017-10-01 00:00:00")));

        System.out.println(DateTools.format(DateTools.calculateByDate(new Date(), 1)));
        System.out.println(DateTools.isBefore(DateTools.stringToDate("2018-1-02 00:00:00"), new Date()));


    }


    /**
     * 函数介绍：根据默认模式包日期对象转换成日期字符串 参数：date ,日期对象；parttern,日期字符格式 返回值：日期字符串
     */
    public static String format(Date date, String parttern) {
        if (date == null) {
            return null;
        }
        DateFormat df = new SimpleDateFormat(parttern);
        return df.format(date);
    }


    /**
     * 函数介绍：根据默认模式包日期对象转换成日期字符串 参数：date ,日期对象 返回值：日期字符串
     */
    public static String format(Date date) {
        return format(date, DEFAULT_DATE_FORMAT);
    }

    /**
     * 格式化时间
     * yyyy-MM-dd HH:mm:ss
     *
     * @param date yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String yyyyMMddHHmmss(Date date) {
        if (date == null) {
            return null;
        }
        return format(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 格式化时间
     * yyyy-MM-dd
     *
     * @param date yyyy-MM-dd
     * @return
     */
    public static String yyyyMMdd(Date date) {
        return format(date, "yyyy-MM-dd");
    }

    /**
     * 格式化时间
     * HH:mm:ss
     *
     * @param date HH:mm:ss
     * @return
     */
    public static String hhmmss(Date date) {
        return format(date, "HH:mm:ss");
    }


    /**
     * @param strDate 需要转换的字符串
     * @param format  需要格式的目标字符串  举例 yyyy-MM-dd
     * @return
     * @throws ParseException
     * @Enclosing_Method : stringToDate
     * @Written by : czq
     * @Creation Date : Oct 27, 2010 2:45:07 PM
     * @version : v1.00
     * @Description : 将字符串转换成日期时间
     */
    public static Date stringToDate(String strDate, String format)
            throws ParseException {
        if (strDate == null) {
            return null;
        }
        DateFormat df = new SimpleDateFormat(format);
        return df.parse(strDate);
    }

    /**
     * @param strDate 时间字符串
     * @param format  格式化器
     * @return
     * @throws ParseException
     * @Enclosing_Method : stringToDate
     * @Written by : czq
     * @Creation Date : Oct 27, 2010 2:46:55 PM
     * @version : v1.00
     * @Description : 将字符串转换成日期时间
     */
    public static Date stringToDate(String strDate, DateFormat format)
            throws ParseException {
        if (strDate == null) {
            return null;
        }
        return format.parse(strDate);
    }

    /**
     * 函数介绍：根据日期字符串转换成日期对象 参数：strDate,日期字符串 返回值：date 对象
     */
    public static Date stringToDate(String strDate) {
        if (strDate == null) {
            return null;
        }
        for (DateFormat df : ACCEPT_DATE_FORMATS) {
            try {
                return df.parse(strDate);
            } catch (Exception e) {
                continue;
            }
        }

        return null;
    }



    /**
     * @param datestr 原时间串
     * @param format  原时间串格式
     * @return
     * @throws ParseException
     * @Enclosing_Method : stringToNum14
     * @Written by : czq
     * @Creation Date : Oct 27, 2010 2:51:13 PM
     * @version : v1.00
     * @Description : 将时间串转换成yyyyMMddHHmmss形式
     */
    public static String stringToNum14(String datestr, String format)
            throws ParseException {
        if (datestr == null || StringUtils.isBlank(datestr.trim())) {
            return null;
        }
        return format(stringToDate(datestr, format), "yyyyMMddHHmmss");
    }



    /**
     * @param datestr 原时间串
     * @param format  原时间串格式
     * @return
     * @throws ParseException
     * @Enclosing_Method : stringToNum8
     * @Written by : czq
     * @Creation Date : Oct 27, 2010 2:51:52 PM
     * @version : v1.00
     * @Description : 将时间串转换成yyyyMMdd 形式
     */
    public static String stringToNum8(String datestr, String format)
            throws ParseException {
        if (datestr == null || StringUtils.isBlank(datestr.trim())) {
            return null;
        }
        return format(stringToDate(datestr, format), "yyyyMMdd");
    }

    public static String stringToNum8(String datestr) {
        if (datestr == null || StringUtils.isBlank(datestr.trim())) {
            return null;
        }
        return format(stringToDate(datestr), "yyyyMMdd");
    }

    public static String stringToNum8(Date date) {
        if (date == null) {
            return null;
        }
        return format(date, "yyyyMMdd");
    }


    /**
     * @param datestr 原时间串
     * @param format  原时间串格式
     * @return
     * @throws ParseException
     * @Enclosing_Method : stringToNum06
     * @Written by : czq
     * @Creation Date : Oct 27, 2010 2:52:47 PM
     * @version : v1.00
     * @Description : 将时间串转换成yyyyMM形式
     */
    public static String stringToNum06(String datestr, String format)
            throws ParseException {
        if (datestr == null || StringUtils.isBlank(datestr.trim())) {
            return null;
        }
        return format(stringToDate(datestr, format), "yyyyMM");
    }

    /**
     * 函数介绍：把日期对象转换成数据库需要数字 参数：日期对象 返回值：数字字符
     */
    public static String dateToNum14(Date date) {
        return format(date, "yyyyMMddHHmmss");
    }

    public static String StringHmsToNum6(String str) {
        return str.replaceAll(":", "");
    }

    /**
     * 得到应用服务器的系统时间.
     *
     * @return yyyymmddhhmiss格式的当前时间
     * @author fanhuifeng
     */
    public static String getDateFormatss() {
        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.YEAR)
                + addZero("" + (calendar.get(Calendar.MONTH) + 1))
                + addZero("" + calendar.get(Calendar.DAY_OF_MONTH))
                + addZero("" + calendar.get(Calendar.HOUR_OF_DAY))
                + addZero("" + calendar.get(Calendar.MINUTE))
                + addZero("" + calendar.get(Calendar.SECOND));
        return date;
    }

    /**
     * 给不够2位的字符串前面加0.
     *
     * @return 字符串，不够两位左补0
     * @author fanhuifeng
     */
    private static String addZero(String s) {
        if (s.length() < 2) {
            return "0" + s;
        }
        return s;
    }

    /**
     * @param sec
     * @return : String
     * @Enclosing_Method : getDateFormatByDifferSeconds
     * @Written by        : wangchuan
     * @Creation Date     : Aug 15, 2011 3:41:04 PM
     * @version : v1.00
     * @Description : 根据秒差计算时间
     */
    public static String getDateFormatByDifferSeconds(int sec) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, sec);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String nowtime = sdf.format(cal.getTime());
        return nowtime;
    }

    /**
     * @param cal
     * @param sec
     * @return : String
     * @Enclosing_Method : getDateFormatByDifferSeconds
     * @Written by        : wangchuan
     * @Creation Date     : Aug 15, 2011 3:42:12 PM
     * @version : v1.00
     * @Description :  根据秒差计算时间
     */
    public static String getDateFormatByDifferSeconds(Calendar cal, int sec) {
        cal.add(Calendar.SECOND, sec);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String nowtime = sdf.format(cal.getTime());
        return nowtime;
    }

    /**
     * ************************************************************************
     * 查询昨天的时间
     *
     * @param day
     * @return
     */
    public static String getDateFormatYesterday(int day) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String nowtime = sdf.format(cal.getTime());
        return nowtime;
    }

    /**
     * ************************************************************************
     * 查询昨天的时间
     *
     * @param day
     * @return
     */
    public static String getDateFormatYesterday(int day, String format) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -day);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String nowtime = sdf.format(cal.getTime());
        return nowtime;
    }

    /**
     * 获取延迟day天后的格式化字符串
     *
     * @param day 后延天数
     * @return yyyyMMddHHmmss 形式的时间字符串
     */
    public static String getDateFormatAfter(int day) {
        return getDateFormatAfter(day, "yyyyMMddHHmmss");
    }

    /**
     * 获取延迟day天后的格式化字符串
     *
     * @param day    后延天数
     * @param format 格式化字符串
     * @return
     */
    public static String getDateFormatAfter(int day, String format) {
        if (format == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, +day);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String nowtime = sdf.format(cal.getTime());
        return nowtime;
    }

    /**
     * 查询前一天的时间
     *
     * @param day
     * @param num
     * @return
     */
    public static String getFormatPreDay(Calendar day, int num) {
        day.add(Calendar.DAY_OF_YEAR, -num);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String nowtime = sdf.format(day.getTime());
        return nowtime;
    }

    /**
     * 查询前一周的时间
     *
     * @param day
     * @param num
     * @return
     */
    public static String getFormatPreWeek(Calendar day, int num) {
        day.add(Calendar.WEEK_OF_YEAR, -num);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String nowtime = sdf.format(day.getTime());
        return nowtime;
    }

    /**
     * 查询前一个月的时间
     *
     * @param day
     * @param num
     * @return
     */
    public static String getFormatPreMonth(Calendar day, int num) {
        day.add(Calendar.MONTH, -num);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String nowtime = sdf.format(day.getTime());
        return nowtime;
    }

    /**
     * 查询前一个月的时间
     *
     * @param day
     * @param num
     * @return
     */
    public static String getFormatPreMonth(Calendar day, int num, String format) {
        if (format == null) {
            return null;
        }
        day.add(Calendar.MONTH, -num);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String nowtime = sdf.format(day.getTime());
        return nowtime;
    }

    /**
     * 查询前一年的时间
     *
     * @param day
     * @param num
     * @return
     */
    public static String getFormatPreYear(Calendar day, int num) {
        day.add(Calendar.YEAR, -num);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String nowtime = sdf.format(day.getTime());
        return nowtime;
    }

    /**
     * 组合Oracle 日期类型
     *
     * @param date
     * @return
     */
    public static String getOracleDate(Date date) {
        if (date == null) {
            return null;
        }
        String updateTime = DateTools.format(date, "yyyy-MM-dd");
        return "to_date('" + updateTime + "','YYYY-MM-DD')";
    }

    /**
     * 组合Oracle 日期类型
     *
     * @param date
     * @return
     */
    public static String getOracleDateTime(Date date) {
        if (date == null) {
            return null;
        }
        String updateTime = DateTools.format(date, "yyyy-MM-dd HH:mm:ss");
        return "to_date('" + updateTime + "','YYYY-MM-DD HH24:MI:SS')";
    }

    /**
     * @param data
     * @return 计算两个日期的天数差异
     */
    public static long getDifferDate(String data) {
        if (data == null) {
            return -1;
        }
        Date newdate = new Date();
        Date date1 = DateTools.stringToDate(data);
        long t1 = newdate.getTime();
        long t2 = date1.getTime();
        long t3 = (t1 - t2) / (60 * 1000);
        return t3;
    }

    /**
     * 获取时间戳
     *
     * @param date 日期
     * @return
     */
    public static long getTimestmp(Date date) {
        if (date == null) {
            return -1;
        }
        return date.getTime();
    }

    /**
     * 日期串格式化成日期
     *
     * @param dateStr 日期字符串
     * @return
     */
    public static Date timestmpToDate(long dateStr) {
        return new Date(dateStr);
    }


    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate) throws ParseException {
        if (smdate == null || bdate == null) {
            return -1;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 字符串的日期格式的计算两个日期相差天数
     */
    public static int daysBetween(String smdate, String bdate) throws ParseException {
        if (smdate == null || bdate == null) {
            return -1;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 计算分钟相差
     *
     * @param smallDate 第一时间
     * @param bigDate   第二时间
     * @return
     */
    public static long diffminutes(String smallDate, String bigDate) {
        if (smallDate == null || bigDate == null) {
            return -1;
        }
        Date begin = stringToDate(smallDate);
        Date end = stringToDate(bigDate);
        long between = (end.getTime() - begin.getTime()) / 1000;//除以1000是为了转换成秒
        long min = between / 60;
        return min;
    }


    /**
     * 时间计算
     *
     * @param dateToNum14
     * @param date_num    相差时间间隔
     * @param date_type   和那位进行计算 [0 年 year] [1 月 month] [2 日 date] [3 时 hour] [4 分 minute] [5 秒 second]
     * @return
     */
    public static Date getSomeOneDay(String dateToNum14, int date_num, int date_type) {
        if (dateToNum14 == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();

        Date config_time = DateTools.stringToDate(dateToNum14);
        c.setTime(config_time);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        switch (date_type) {
            case 0: {
                c.set(year + date_num, month, date, hour, minute, second);
            }
            break;
            case 1: {
                c.set(year, month + date_num, date, hour, minute, second);
            }
            break;
            case 2: {
                c.set(year, month, date + date_num, hour, minute, second);
            }
            break;
            case 3: {
                c.set(year, month, date, hour + date_num, minute, second);
            }
            break;
            case 4: {
                c.set(year, month, date, hour, minute + date_num, second);
            }
            break;
            case 5: {
                c.set(year, month, date, hour, minute, second + date_num);
            }
            break;
            default: {
                c.set(year, month, date, hour, minute, second);
            }
            break;
        }
        return c.getTime();
    }

    /**
     * @param dateToNum14
     * @param timeStr     例如 1s/S 1秒，30m 分钟 ，12h/H 小时，2w/W 两周, 15d/D 15天 ，1M 一个月，1y/Y 一年
     * @return
     */
    public static Date getSomeOneDay(String dateToNum14, String timeStr) {
        if (dateToNum14 == null || timeStr == null) {
            return null;
        }
        int time = 0;
        int type = -1;
        if (timeStr.toLowerCase().contains("s")) {
            time = Integer.parseInt(timeStr.replace("s", ""));
            type = 5;
        } else if (timeStr.contains("m")) {
            time = Integer.parseInt(timeStr.replace("m", ""));
            type = 4;
        } else if (timeStr.toLowerCase().contains("h")) {
            time = Integer.parseInt(timeStr.replace("h", ""));
            type = 3;
        } else if (timeStr.toLowerCase().contains("d")) {
            time = Integer.parseInt(timeStr.replace("d", ""));
            type = 2;
        } else if (timeStr.toLowerCase().contains("w")) {
            time = Integer.parseInt(timeStr.replace("m", ""));
            type = 2;
        } else if (timeStr.toLowerCase().contains("M")) {
            time = Integer.parseInt(timeStr.replace("M", ""));
            type = 1;
        } else if (timeStr.toLowerCase().contains("y")) {
            time = Integer.parseInt(timeStr.replace("y", ""));
            type = 0;
        } else {
            return null;
        }
        return getSomeOneDay(dateToNum14, time, type);
    }

    /**
     * @param timeStr 例如 1s/S 1秒，30m 分钟 ，12h/H 小时，2w/W 两周, 15d/D 15天 ，1M 一个月，1y/Y 一年
     * @return
     */
    public static long getMilliseconds(String timeStr) {
        if (timeStr == null) {
            return -1;
        }
        //计算预期时间
        Date someday = getSomeOneDay(DateTools.dateToNum14(new Date()), timeStr);
        //计算预期时间和当前时间的毫秒数差
        return DateTools.secondsBetween(DateTools.dateToNum14(someday), DateTools.dateToNum14(new Date())) * 1000;
    }

    /**
     * 获取传入年月的天数
     *
     * @param year
     * @param month
     * @return
     */
    public static int getSomeOneMonthDays(int year, int month) {
        int days = 0;
        Calendar c = Calendar.getInstance();
        c.set(year, month, 0);
        days = c.get(Calendar.DATE);
        return days;
    }

    /**
     * 获得月最后一天
     *
     * @param year
     * @param month
     * @return
     */
    public static Date getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month + 1);
        cal.set(Calendar.DAY_OF_MONTH, 0);
        return cal.getTime();
    }

    /**
     * 获得月第一天
     *
     * @param year
     * @param month
     * @return
     */
    public static Date getFirstDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }


    /**
     * 获得月份差
     *
     * @param day1
     * @param day2
     * @return
     */
    public static int monthBetween(String day1, String day2) {
        if (day1 == null || day2 == null) {
            return -1;
        }
        int differ = 0;
        try {
            Calendar c = Calendar.getInstance();
            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            //年1
            Date d1 = df.parse(day1);
            c.setTime(d1);
            int y1 = c.get(Calendar.YEAR);
            int m1 = c.get(Calendar.MONTH);
            //年2
            Date d2 = df.parse(day2);
            c.setTime(d2);
            int y2 = c.get(Calendar.YEAR);
            int m2 = c.get(Calendar.MONTH);
            //结果
            differ = 12 * (y1 - y2) + (m1 - m2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return differ;//两个日期相差几个月，即月份差;
    }

    /**
     * 计算今天和传入的月份差
     */
    public static int getDifferMonth(String day) {
        if (day == null) {
            return -1;
        }
        String currentDay = DateTools.stringToNum8(DateTools.dateToNum14(new Date()));
        return DateTools.monthBetween(currentDay, day);
    }

    /**
     * 获得秒数差
     *
     * @param d1 格式：yyyyMMddHHmmss
     * @param d2 格式：yyyyMMddHHmmss
     * @return
     */
    public static long secondsBetween(String d1, String d2) {
        if (d1 == null || d2 == null) {
            return -1;
        }
        long differ = 0;
        try {
            DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            //年1
            Date dd1 = df.parse(d1);
            //年2
            Date dd2 = df.parse(d2);
            //结果
            differ = dd1.getTime() - dd2.getTime();
            ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return differ / 1000;//两个日期相差几秒，即秒差;
    }

    /**
     * 计算今天距离周日的时间差毫秒数
     *
     * @return
     */
    public static long getSundayDifference() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
        Calendar cal = Calendar.getInstance();
        Date nowDate = new Date();
        cal.setTime(nowDate);
        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            return 0L;
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, 6);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis() - nowDate.getTime();
    }

    /**
     * 根据一天计算一周的周一
     *
     * @param today
     * @return
     */
    public static String getMondayByToday(String today) {
        if (today == null) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); //设置时间格式
            Calendar cal = Calendar.getInstance();
            Date time = sdf.parse(today);
            cal.setTime(time);
            System.out.println("要计算日期为:" + sdf.format(cal.getTime())); //输出要计算日期

            //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
            int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
            if (1 == dayWeek) {
                cal.add(Calendar.DAY_OF_MONTH, -1);
            }
            cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
            int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
            cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
            String monday = sdf.format(cal.getTime());
            System.out.println("所在周星期一的日期：" + monday);
            cal.add(Calendar.DATE, 6);
            String sunday = sdf.format(cal.getTime());
            System.out.println("所在周星期日的日期：" + sunday);
            return monday;

        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 根据一天计算一周的周日
     *
     * @param today
     * @return
     */
    public static String getSundayByToday(String today) {
        if (today == null) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); //设置时间格式
            Calendar cal = Calendar.getInstance();
            Date time = sdf.parse(today);
            cal.setTime(time);
            System.out.println("要计算日期为:" + sdf.format(cal.getTime())); //输出要计算日期

            //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
            int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
            if (1 == dayWeek) {
                cal.add(Calendar.DAY_OF_MONTH, -1);
            }
            cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
            int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
            cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
            String monday = sdf.format(cal.getTime());
            System.out.println("所在周星期一的日期：" + monday);
            cal.add(Calendar.DATE, 6);
            String sunday = sdf.format(cal.getTime());
            System.out.println("所在周星期日的日期：" + sunday);
            return sunday;

        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 根据一天计算一个月的第一天
     *
     * @param today
     * @return
     */
    public static String getFirstDayOfMonth(String today) {
        if (today == null) {
            return null;
        }
        try {
            int year = Integer.parseInt(today.substring(0, 4));
            int month = Integer.parseInt(today.substring(4, 6));
            String fisrtday = DateTools.dateToNum14(DateTools.getFirstDayOfMonth(year, month - 1));
            return fisrtday;

        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 根据一天计算一个月的最后一天
     *
     * @param today
     * @return
     */
    public static String getLastDayOfMonth(String today) {
        if (today == null) {
            return null;
        }
        try {
            int year = Integer.parseInt(today.substring(0, 4));
            int month = Integer.parseInt(today.substring(4, 6));
            String lastday = DateTools.dateToNum14(DateTools.getLastDayOfMonth(year, month - 1));
            return lastday;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 比较两个日期大小，即前一个日期是否早于后一个日期
     * 如果早于那么返回1，晚于返回-1,相等返回0,否则返回null
     *
     * @param date1
     * @param date2
     * @return Integer
     * @version 1.0.0
     * @2017-3-22 15:04:17
     */
    public static Integer isBefore(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return null;
        }
        Integer result = 0;
        try {
            if (date1.compareTo(date2) < 0) {//早
                result = 1;
            } else if (date1.compareTo(date2) > 0) {
                result = -1;
            }
        } catch (Exception e) {
            result = null;
        }
        return result;
    }


    /**
     * 获取当前年
     *
     * @version 1.0.0
     * @2017-3-22 16:13:15
     */
    public static Integer getCurYear() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        Integer curYear = c.get(Calendar.YEAR);
        return curYear;
    }

    /**
     * 获取当前月
     *
     * @version 1.0.0
     * @2017-3-22 16:13:20
     */
    public static Integer getCurMonth() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return c.get(Calendar.MONTH);
    }

    /**
     * 获取当前日期
     *
     * @version 1.0.0
     * @2017-3-22 16:13:24
     */
    public static Integer getCurDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return c.get(Calendar.DATE);
    }

    /**
     * 获取当前周几
     *
     * @version 1.0.0
     * @2017-3-22 16:13:29
     */
    public static String getCurDay() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return weekDay[c.get(Calendar.DAY_OF_WEEK) - 1];
    }


    /**
     * 由出生日期获得年龄,精确到具体日期
     *
     * @param birthDay
     * @return
     * @throws Exception
     */
    public static int getAge(Date birthDay) {
        if (birthDay == null) {
            return -1;
        }
        Calendar cal = Calendar.getInstance();
        int age = 0;
        if (cal.before(birthDay)) {
            return age;
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }
        return age;
    }


    /**
     * 根据年龄获取出生日期
     *
     * @param age 年龄
     * @return
     */
    public static Date getBirthday(Integer age) {
        if (age == null) {
            return null;
        }
        Calendar curCalendar = Calendar.getInstance();           //获取现在时间
        int curYear = curCalendar.get(Calendar.YEAR);//获取年份
        int curMonth = curCalendar.get(Calendar.MONTH);//获取月份
        int day = curCalendar.get(Calendar.DAY_OF_MONTH);//日期
        int birthdayYear = curYear - age;
        Date birthday = null;
        try {
            String birthdayDate = String.valueOf(birthdayYear) + "-" + String.valueOf(curMonth) + "-" + String.valueOf(day);
            birthday = stringToDate(String.valueOf(birthdayDate), DEFAULT_DATE_FORMAT);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return birthday;
    }


    /**
     * 对日期(时间)中的日进行加减计算. <br>
     * 例子: <br>
     * 如果Date类型的d为 2005年8月20日,那么 <br>
     * calculateByDate(d,-10)的值为2005年8月10日 <br>
     * 而calculateByDate(d,+10)的值为2005年8月30日 <br>
     *
     * @param d      日期(时间).
     * @param amount 加减计算的幅度.+n=加n天;-n=减n天.
     * @return 计算后的日期(时间).
     */
    public static Date calculateByDate(Date d, int amount) {
        if (d == null) {
            return null;
        }
        return calculate(d, GregorianCalendar.DATE, amount);
    }

    public static Date calculateByMinute(Date d, int amount) {
        if (d == null) {
            return null;
        }
        return calculate(d, GregorianCalendar.MINUTE, amount);
    }

    public static Date calculateByMonth(Date d, int amount) {
        if (d == null) {
            return null;
        }
        return calculate(d, GregorianCalendar.MONTH, amount);
    }

    public static Date calculateByYear(Date d, int amount) {
        if (d == null) {
            return null;
        }
        return calculate(d, GregorianCalendar.YEAR, amount);
    }

    /**
     * 对日期(时间)中由field参数指定的日期成员进行加减计算. <br>
     * 例子: <br>
     * 如果Date类型的d为 2005年8月20日,那么 <br>
     * calculate(d,GregorianCalendar.YEAR,-10)的值为1995年8月20日 <br>
     * 而calculate(d,GregorianCalendar.YEAR,+10)的值为2015年8月20日 <br>
     *
     * @param d      日期(时间).
     * @param field  日期成员. <br>
     *               日期成员主要有: <br>
     *               年:GregorianCalendar.YEAR <br>
     *               月:GregorianCalendar.MONTH <br>
     *               日:GregorianCalendar.DATE <br>
     *               时:GregorianCalendar.HOUR <br>
     *               分:GregorianCalendar.MINUTE <br>
     *               秒:GregorianCalendar.SECOND <br>
     *               毫秒:GregorianCalendar.MILLISECOND <br>
     * @param amount 加减计算的幅度.+n=加n个由参数field指定的日期成员值;-n=减n个由参数field代表的日期成员值.
     * @return 计算后的日期(时间).
     */
    private static Date calculate(Date d, int field, int amount) {
        if (d == null) {
            return null;
        }
        GregorianCalendar g = new GregorianCalendar();
        g.setTime(d);
        g.add(field, amount);
        return g.getTime();
    }

    /**
     * 日期(时间)转化为字符串.
     *
     * @param formater 日期或时间的格式.
     * @param aDate    java.util.Date类的实例.
     * @return 日期转化后的字符串.
     */
    public static String date2String(String formater, Date aDate) {
        if (formater == null || "".equals(formater)) {
            return null;
        }
        if (aDate == null) {
            return null;
        }
        return (new SimpleDateFormat(formater)).format(aDate);
    }

    /**
     * 当前日期(时间)转化为字符串.
     *
     * @param formater 日期或时间的格式.
     * @return 日期转化后的字符串.
     */
    public static String date2String(String formater) {
        if (formater == null) {
            return null;
        }
        return date2String(formater, new Date());
    }

    /**
     * 获取当前日期对应的星期数.
     * <br>1=星期天,2=星期一,3=星期二,4=星期三,5=星期四,6=星期五,7=星期六
     *
     * @return 当前日期对应的星期数
     */
    public static int dayOfWeek() {
        GregorianCalendar g = new GregorianCalendar();
        int ret = g.get(Calendar.DAY_OF_WEEK);
        g = null;
        return ret;
    }


    /**
     * 获取所有的时区编号. <br>
     * 排序规则:按照ASCII字符的正序进行排序. <br>
     * 排序时候忽略字符大小写.
     *
     * @return 所有的时区编号(时区编号已经按照字符[忽略大小写]排序).
     */
    public static String[] fecthAllTimeZoneIds() {
        Vector v = new Vector();
        String[] ids = TimeZone.getAvailableIDs();
        for (int i = 0; i < ids.length; i++) {
            v.add(ids[i]);
        }
        Collections.sort(v, String.CASE_INSENSITIVE_ORDER);
        v.copyInto(ids);
        v = null;
        return ids;
    }

    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日16时09分00秒"）
     *
     * @param timeStamp 时间戳
     * @return
     */
    public static String times(String timeStamp) {
        if (timeStamp == null) {
            return null;
        }
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        int i = Integer.parseInt(timeStamp);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日16时09分00秒"）
     *
     * @param timeStamp 时间戳
     * @param pattern   格式化日期格式
     * @return
     */
    public static String times(String timeStamp, String pattern) {
        if (timeStamp == null || pattern == null) {
            return null;
        }
        SimpleDateFormat sdr = new SimpleDateFormat(pattern);
        int i = Integer.parseInt(timeStamp);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }


    /**
     * 调用此方法输入所要转换的时间戳例如（1402733340）输出（"2014年06月14日16时09分00秒"）
     *
     * @param timeStamp 时间戳
     * @return
     */
    public static String times(long timeStamp) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdr.format(new Date(timeStamp));
    }

    /**
     * 将日期时间字符串根据转换为指定时区的日期时间.
     *
     * @param srcFormater   待转化的日期时间的格式.
     * @param srcDateTime   待转化的日期时间.
     * @param dstFormater   目标的日期时间的格式.
     * @param dstTimeZoneId 目标的时区编号.
     * @return 转化后的日期时间.
     */
    public static String string2Timezone(String srcFormater,
                                         String srcDateTime, String dstFormater, String dstTimeZoneId) {
        if (srcFormater == null || "".equals(srcFormater)) {
            return null;
        }
        if (srcDateTime == null || "".equals(srcDateTime)) {
            return null;
        }
        if (dstFormater == null || "".equals(dstFormater)) {
            return null;
        }
        if (dstTimeZoneId == null || "".equals(dstTimeZoneId)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(srcFormater);
        try {
            int diffTime = getDiffTimeZoneRawOffset(dstTimeZoneId);
            Date d = sdf.parse(srcDateTime);
            long nowTime = d.getTime();
            long newNowTime = nowTime - diffTime;
            d = new Date(newNowTime);
            return date2String(dstFormater, d);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } finally {
            sdf = null;
        }
    }


    /**
     * 获取系统当前默认时区与UTC的时间差.(单位:毫秒)
     *
     * @return 系统当前默认时区与UTC的时间差.(单位:毫秒)
     */
    private static int getDefaultTimeZoneRawOffset() {
        return TimeZone.getDefault().getRawOffset();
    }

    /**
     * 获取指定时区与UTC的时间差.(单位:毫秒)
     *
     * @param timeZoneId 时区Id
     * @return 指定时区与UTC的时间差.(单位:毫秒)
     */
    private static int getTimeZoneRawOffset(String timeZoneId) {
        if (timeZoneId == null) {
            return -1;
        }
        return TimeZone.getTimeZone(timeZoneId).getRawOffset();
    }

    /**
     * 获取系统当前默认时区与指定时区的时间差.(单位:毫秒)
     *
     * @param timeZoneId 时区Id
     * @return 系统当前默认时区与指定时区的时间差.(单位:毫秒)
     */
    private static int getDiffTimeZoneRawOffset(String timeZoneId) {
        if (timeZoneId == null) {
            return -1;
        }
        return TimeZone.getDefault().getRawOffset()
                - TimeZone.getTimeZone(timeZoneId).getRawOffset();
    }

    /**
     * 将日期时间字符串根据转换为指定时区的日期时间.
     *
     * @param srcDateTime   待转化的日期时间.
     * @param dstTimeZoneId 目标的时区编号.
     * @return 转化后的日期时间.
     * @see #string2Timezone(String, String, String, String)
     */
    public static String date2Timezone(String srcDateTime,
                                       String dstTimeZoneId) {
        return string2Timezone("yyyy-MM-dd HH:mm:ss", srcDateTime,
                "yyyy-MM-dd HH:mm:ss", dstTimeZoneId);
    }

    public static String date2Timezone(Date srcDateTime,
                                       String dstTimeZoneId) {
        Date timeZoneDate = utcToDate(format(srcDateTime, "yyyy-MM-dd HH:mm:ss"));
        return string2Timezone("yyyy-MM-dd HH:mm:ss", format(timeZoneDate, "yyyy-MM-dd HH:mm:ss"),
                "yyyy-MM-dd HH:mm:ss", dstTimeZoneId);
    }


    /**
     * UTC时间转时区时间格式
     *
     * @param utcTime  UTC时间
     * @param timeZone 时区
     * @param patten   时间格式
     * @return 时区时间格式的时间
     * eg:utc2Local("2017-06-14 09:37:50.788+08:00", "yyyy-MM-dd HH:mm:ss.SSSXXX", "yyyy-MM-dd HH:mm:ss.SSS")
     */
    public static String utc2TimeZone(String utcTime, String timeZone, String patten) {
        if (utcTime == null || timeZone == null || patten == null) {
            return null;
        }
        SimpleDateFormat utcFormater = new SimpleDateFormat(patten);
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));//时区定义并进行时间获取
        Date gpsUTCDate = null;
        try {
            gpsUTCDate = utcFormater.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return utcTime;
        }
        SimpleDateFormat tzFormater = new SimpleDateFormat(patten);
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        tzFormater.setTimeZone(tz);
        String tzTime = tzFormater.format(gpsUTCDate.getTime());
        return tzTime;
    }

    /**
     * UTC时间转时区时间格式
     *
     * @param utcTime  UTC时间
     * @param timeZone 时区
     * @return 时区时间格式的时间
     */
    public static String utc2TimeZone(String utcTime, String timeZone) {
        return utc2TimeZone(utcTime, timeZone, "yyyy-MM-dd HH:mm:ss");
    }

    public static String utc2TimeZone(Date utcTime, String timeZone) {
        return utc2TimeZone(format(utcTime, "yyyy-MM-dd HH:mm:ss"), timeZone, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获得UTC 时间
     *
     * @param date 时间
     * @return UTC
     */
    public static Date toUtc(Date date) {
        if (date == null) {
            return null;
        }
        String utcStr = date2Timezone(format(date, "yyyy-MM-dd HH:mm:ss"), "UTC");
        return stringToDate(utcStr);
    }

    /**
     * 获得UTC 时间，yyyy-MM-dd'T'HH:mm'Z'
     *
     * @param date 时间
     * @return UTC yyyy-MM-dd'T'HH:mm'Z'
     */
    public static String toUtcFmt(Date date) {
        if (date == null) {
            return null;
        }
        return format(date, "yyyy-MM-dd'T'HH:mm:ss'Z'");
    }

    /**
     * 将现有utc时间格式转成日期格式
     * yyyy-MM-dd'T'HH:mm'Z' -> yyyy-MM-dd HH:mm:ss
     *
     * @param utcDate
     * @return
     */
    public static Date utcToDate(String utcDate) {
        if (utcDate == null) {
            return null;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return df.parse(utcDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获得指定时区的时间时间对象
     *
     * @param dateStr  时间格式
     * @param timeZone 时区
     * @return Date
     */
    public static Date getTimeZoneDate(String dateStr, String timeZone) {
        try {
            if (dateStr == null || timeZone == null) {
                return null;
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
            Date date = simpleDateFormat.parse(dateStr);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获得指定时区的时间时间
     *
     * @param dateStr  时间格式
     * @param timeZone 时区
     * @return Date
     */
    public static Date toUtc(String dateStr, String timeZone) {
        if (dateStr == null || timeZone == null) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        Date date1 = null;
        try {
            date1 = simpleDateFormat.parse(dateStr);
            return toUtc(date1);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }


    }

    public static Date toUtc(Date date, String timeZone) {
        if (date == null || timeZone == null) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        Date date1 = null;
        try {
            date1 = simpleDateFormat.parse(DateTools.format(date, "yyyy-MM-dd HH:mm:ss"));
            return toUtc(date1);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }


    }


    /**
     * 获取时间的上下午
     *
     * @param date 时间
     * @return
     */
    public static String getAM_PM(Date date) {
        GregorianCalendar ca = new GregorianCalendar();
        ca.setTime(date);
        return ca.get(GregorianCalendar.AM_PM) == 0 ? "AM" : "PM";
    }


}
