package com.github.yu000hong.spring.common.util

import java.sql.Date
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

/**
 * 只处理常见的两种日期格式:
 * - yyyy-MM-dd HH:mm:ss
 * - yyyy-MM-dd
 */
class DateUtil {

    /**
     * 每个线程独占使用的Timestamp格式化对象
     */
    public static final ThreadLocal<SimpleDateFormat> timeFormatter = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')
        }
    }

    /**
     * 每个线程独占使用的Date格式化对象
     */
    public static final ThreadLocal<SimpleDateFormat> dateFormatter = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            new SimpleDateFormat('yyyy-MM-dd')
        }
    }

    /**
     * 格式化日期
     * @param date 日期
     * @return
     */
    public static String format(java.util.Date date) {
        return dateFormatter.get().format(date)
    }

    /**
     * 格式化时间
     * @param time 时间
     * @return
     */
    public static String format(Timestamp time) {
        return timeFormatter.get().format(time)
    }

    /**
     * 解析日期字符串
     * @param date 日期
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String date) {
        return new Date(dateFormatter.get().parse(date).time)
    }

    /**
     * 解析时间字符串
     * @param time 时间
     * @return
     * @throws ParseException
     */
    public static Timestamp parseTime(String time) {
        return new Timestamp(timeFormatter.get().parse(time).time)
    }

    /**
     * 把各种时间转成成秒单位
     * @param time 时间
     * @param unit 单位
     * @return
     */
    public static long toSeconds(long time, TimeUnit unit) {
        switch (unit) {
            case TimeUnit.DAYS:
                return time * 24 * 60 * 60
            case TimeUnit.HOURS:
                return time * 60 * 60
            case TimeUnit.MINUTES:
                return time * 60
            case TimeUnit.SECONDS:
                return time
            case TimeUnit.MILLISECONDS:
                return time / 1000
            default:
                throw new IllegalArgumentException("invalid time unit: $unit")
        }
    }

}
