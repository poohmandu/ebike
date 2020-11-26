/*
 * Copyright 2020 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qdigo.ebike.iotcenter.util;


import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.SystemException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 可以进行以下方式的日期格式化
 * @see DateUtil#HOUR_MINUTE_SECOND_PATTERN
 * @see DateUtil#YMDHMS_PATTERN
 * @see DateUtil#YMDHMS_LIST_PATTERN
 * 
 */
public class DateUtil {

    /**
     * format pattern is "yyyyMM"
     */
    public static final String YEAR_MONTH_PATTERN = "yyyyMM";
    /**
     * format pattern is "yyyy-MM-dd"
     */
    public static final String YEAR_MONTH_DAY_PATTERN = "yyyy-MM-dd";
    /**
     * format pattern is "HH:mm:ss"
     */
    public static final String HOUR_MINUTE_SECOND_PATTERN = "HH:mm:ss";
    /**
     * format pattern is "HH:mm:ss"
     */
    public static final String YMDHMS_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * format pattern is "yyyyMMddHHmmss"
     */
    public static final String YMDHMS_LIST_PATTERN = "yyyyMMddHHmmss";
    
    public static final String YMDHMSS_LIST_PATTERN = "yyyyMMddHHmmssSSS";

    /**
     * format pattern is "yyyyMMddHHmmss"
     */
    public static final String YMDHMS_ORACLE_LIST_PATTERN = "yyyyMMddHH24miss";
    
    public static final String YEAR_MONTH_DAY_STRAIGHT_PATTERN = "yyyyMMdd";

    public static final String YMDHM_PATTERN = "yyyy/MM/dd HH:mm";

    public static final String YMDHMS_PATTERN_2 = "yyyy/MM/dd HH:mm:ss";
    

    /**
     * default pattern is "yyyy-MM-dd"
     */
    public static final String DEFAULT_PATTERN = YMDHMS_PATTERN;
    private static Calendar c;

    static {
        c = Calendar.getInstance();
    }


    /**
     * 获取系统当前时间
     *
     * @return 系统当前时间
     * @throws SQLException 兼容版本
     * @throws SystemException 兼容版本
     */
    public static Date currentDate() {
        return new Date();
    }

    /**
     * 返回给定格式pattern的日期，类型为String
     * @param pattern 日期格式
     * @return 字符串形式的日期
     * @throws SQLException
     * @throws SystemException
     */
    public static String currentDateString(final String pattern) {
        return format(currentDate(), pattern);
    }

    /**
     * 默认格式的字符串型日期，默认格式的日期格式为yyyy-MM-dd
     * @return 字符串型日期
     * @throws SQLException
     * @throws SystemException
     */
    public static String currentDateDefaultString() {
        return format(currentDate(), DEFAULT_PATTERN);
    }

    /**
     * 字符串形式的日期，格式为yyyyMMddHHmmss
     * @return 字符串类型的日期
     * @throws SQLException
     * @throws SystemException
     */
    public static String currentDateIDString() {
        return format(currentDate(), YMDHMS_LIST_PATTERN);
    }

    /**
     * 字符串形式的日期，格式为yyyy-mm-dd hh:mm:ss:sss
     * @return 字符串类型的日期
     */
    public static String currentDateStringMin() {
    	/*SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
    	sdf.format(date);*/
        return format(currentDate(), YMDHMSS_LIST_PATTERN);
    }

    /**
     * 取得给定时间的年份
     * @param date 输入的日期类型 java.util.Date
     * @return 年份的数字形式，类型为int
     */
    public static int getYear(final Date date) {
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    /**
     * 取得当前年份
     * @return 当前年份
     */
    public static int getCurrentYear() {
        c.setTime(new Date());
        return c.get(Calendar.YEAR);
    }

    /**
     * 取得给定时间的月份，如1，2，3...12等
     * @param date 输入的日期
     * @return 月份值
     */
    public static int getMonth(final Date date) {
        c.setTime(date);
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * 取当前月份
     * @return 当前月份
     */
    public static int getCurrentMonth() {
        c.setTime(new Date());
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * 取得给定时间的天数
     * @param date 输入的日期
     * @return 天数
     */
    public static int getDay(final Date date) {
        c.setTime(date);
        return c.get(Calendar.DATE);
    }

    /**
     * 取当前日期
     * @return 当前天数
     */
    public static int getCurrentDay() {
        c.setTime(new Date());
        return c.get(Calendar.DATE);
    }

    /**
     * 取当前时间
     * @return 当前小时数
     */
    public static int getCurrentHour() {
        c.setTime(new Date());
        return c.get(Calendar.HOUR);
    }

    /**
     * 取当前分钟数
     * @return 当前分钟数
     */
    public static int getCurrentMinute() {
        c.setTime(new Date());
        return c.get(Calendar.MINUTE);
    }

    /**
     * 取当前秒数
     * @return 当前秒数
     */
    public static int getCurrentSecond() {
        c.setTime(new Date());
        return c.get(Calendar.SECOND);
    }

    /**
     * 取得给定时间的小时数
     * @param date 输入的日期
     * @return
     */
    public static int getHour(final Date date) {
        c.setTime(date);
        return c.get(Calendar.HOUR);
    }

    /**
     * 取得给定时间的分钟数
     * @param date 输入的日期
     * @return 分钟数
     */
    public static int getMinute(final Date date) {
        c.setTime(date);
        return c.get(Calendar.MINUTE);
    }

    /**
     * 取得给定时间的秒数
     * @param date 输入的日期
     * @return 秒数
     */
    public static int getSecond(final Date date) {
        c.setTime(date);
        return c.get(Calendar.SECOND);
    }

    /**
     *
     * @param date 输入的日期
     * @return
     */
    public static Integer getYearMonth(final Date date) {
        return new Integer(format(date, YEAR_MONTH_PATTERN));
    }

    /**
     *
     * @param yearMonth
     * @return
     * @throws ParseException
     */
    public static Date parseYearMonth(final Integer yearMonth)
            throws ParseException {
        return parse(String.valueOf(yearMonth), YEAR_MONTH_PATTERN);
    }

    /**
     *  在日期上加上整年
     * @param date 给定日期
     * @param ammount 年数
     * @return 日期
     */
    public static Date addYear(final Date date, final int ammount) {

        c.setTime(date);
        c.add(Calendar.YEAR, ammount);
        return c.getTime();
    }

    /**
     * 在日期上加上整月
     * @param date 给定日期
     * @param ammount 月数
     * @return 日期
     */
    public static Date addMonth(final Date date, final int ammount) {
        c.setTime(date);
        c.add(Calendar.MONTH, ammount);
        return c.getTime();
    }

    /**
     * 在日期上加上整数天
     * @param date 给定日期
     * @param ammount 天数
     * @return 日期
     */
    public static Date addDay(final Date date, final int ammount) {
        c.setTime(date);
        c.add(Calendar.DATE, ammount);
        return c.getTime();
    }

    /**
     *
     * @param yearMonth
     * @param ammount
     * @return
     * @throws ParseException
     */
    public static Integer addMonth(final Integer yearMonth, final int ammount)
            throws ParseException {
        return getYearMonth(addMonth(parseYearMonth(yearMonth), ammount));
    }

    /**
     * 比较给定时间的年份，如果beforeDate的年大于afterDate的年份返回-1，相等返回0，小于返回1
     * @param beforeDate 时间
     * @param afterDate 时间
     * @return 0 ，正数或者负数
     */
    public static int beforeYears(final Date beforeDate, final Date afterDate) {
        Calendar beforeCalendar = c;
        beforeCalendar.setTime(beforeDate);
        beforeCalendar.set(Calendar.MONTH, 1);
        beforeCalendar.set(Calendar.DATE, 1);
        beforeCalendar.set(Calendar.HOUR, 0);
        beforeCalendar.set(Calendar.SECOND, 0);
        beforeCalendar.set(Calendar.MINUTE, 0);
        Calendar afterCalendar = Calendar.getInstance();
        afterCalendar.setTime(afterDate);
        afterCalendar.set(Calendar.MONTH, 1);
        afterCalendar.set(Calendar.DATE, 1);
        afterCalendar.set(Calendar.HOUR, 0);
        afterCalendar.set(Calendar.SECOND, 0);
        afterCalendar.set(Calendar.MINUTE, 0);
        boolean positive = true;
        if (beforeDate.after(afterDate)) {
            positive = false;
        }
        int beforeYears = 0;
        while (true) {
            boolean yearEqual = beforeCalendar.get(Calendar.YEAR) == afterCalendar.get(Calendar.YEAR);
            if (yearEqual) {
                break;
            } else {
                if (positive) {
                    beforeYears++;
                    beforeCalendar.add(Calendar.YEAR, 1);
                } else {
                    beforeYears--;
                    beforeCalendar.add(Calendar.YEAR, -1);
                }
            }
        }
        return beforeYears;
    }

    /**
     * 比较给定时间的月份(含有年份的比较)，如果beforeDate的月份大于afterDate的返回-1，相等返回0，小于返回1
     * @param beforeDate 时间
     * @param afterDate 时间
     * @return  0 ，正数或者负数
     */
    public static int beforeMonths(final Date beforeDate, final Date afterDate) {
        Calendar beforeCalendar = c;
        beforeCalendar.setTime(beforeDate);
        beforeCalendar.set(Calendar.DATE, 1);
        beforeCalendar.set(Calendar.HOUR, 0);
        beforeCalendar.set(Calendar.SECOND, 0);
        beforeCalendar.set(Calendar.MINUTE, 0);
        Calendar afterCalendar = c;
        afterCalendar.setTime(afterDate);
        afterCalendar.set(Calendar.DATE, 1);
        afterCalendar.set(Calendar.HOUR, 0);
        afterCalendar.set(Calendar.SECOND, 0);
        afterCalendar.set(Calendar.MINUTE, 0);
        boolean positive = true;
        if (beforeDate.after(afterDate)) {
            positive = false;
        }
        int beforeMonths = 0;
        while (true) {
            boolean yearEqual = beforeCalendar.get(Calendar.YEAR) == afterCalendar.get(Calendar.YEAR);
            boolean monthEqual = beforeCalendar.get(Calendar.MONTH) == afterCalendar.get(Calendar.MONTH);
            if (yearEqual && monthEqual) {
                break;
            } else {
                if (positive) {
                    beforeMonths++;
                    beforeCalendar.add(Calendar.MONTH, 1);
                } else {
                    beforeMonths--;
                    beforeCalendar.add(Calendar.MONTH, -1);
                }
            }
        }
        return beforeMonths;
    }

    /**
     * 比较两个给定日期的日的关系(含有年月比较)，如果beforeDate的日期大于afterDate返回-1，相等返回0，小于返回1
     * @param beforeDate 日期
     * @param afterDate 日期
     * @return 0，正数或者负数
     */
    public static int beforeDays(final Date beforeDate, final Date afterDate) {
        Calendar beforeCalendar = c;
        beforeCalendar.setTime(beforeDate);
        beforeCalendar.set(Calendar.HOUR, 0);
        beforeCalendar.set(Calendar.SECOND, 0);
        beforeCalendar.set(Calendar.MINUTE, 0);
        Calendar afterCalendar = Calendar.getInstance();
        afterCalendar.setTime(afterDate);
        afterCalendar.set(Calendar.HOUR, 0);
        afterCalendar.set(Calendar.SECOND, 0);
        afterCalendar.set(Calendar.MINUTE, 0);
        boolean positive = true;
        if (beforeDate.after(afterDate)) {
            positive = false;
        }
        int beforeDays = 0;
        while (true) {
            boolean yearEqual = beforeCalendar.get(Calendar.YEAR) == afterCalendar.get(Calendar.YEAR);
            boolean monthEqual = beforeCalendar.get(Calendar.MONTH) == afterCalendar.get(Calendar.MONTH);
            boolean dayEqual = beforeCalendar.get(Calendar.DATE) == afterCalendar.get(Calendar.DATE);
            if (yearEqual && monthEqual && dayEqual) {
                break;
            } else {
                if (positive) {
                    beforeDays++;
                    beforeCalendar.add(Calendar.DATE, 1);
                } else {
                    beforeDays--;
                    beforeCalendar.add(Calendar.DATE, -1);
                }
            }
        }
        return beforeDays;
    }

    /**
     * 获取beforeDate和afterDate之间相差的完整年数，精确到天。负数表示晚。
     * 
     * @param beforeDate 要比较的早的日期
     * @param afterDate 要比较的晚的日期
     * @return beforeDate比afterDate早的完整年数，负数表示晚。
     */
    public static int beforeRoundYears(final Date beforeDate, final Date afterDate) {
        Date bDate = beforeDate;
        Date aDate = afterDate;
        boolean positive = true;
        if (beforeDate.after(afterDate)) {
            positive = false;
            bDate = afterDate;
            aDate = beforeDate;
        }
        int beforeYears = beforeYears(bDate, aDate);

        int bMonth = getMonth(bDate);
        int aMonth = getMonth(aDate);
        if (aMonth < bMonth) {
            beforeYears--;
        } else if (aMonth == bMonth) {
            int bDay = getDay(bDate);
            int aDay = getDay(aDate);
            if (aDay < bDay) {
                beforeYears--;
            }
        }

        if (positive) {
            return beforeYears;
        } else {
            return new BigDecimal(beforeYears).negate().intValue();
        }
    }

    /**
     * 获取beforeDate和afterDate之间相差的完整年数，精确到月。负数表示晚。
     * 
     * @param beforeDate 要比较的早的日期
     * @param afterDate 要比较的晚的日期
     * @return beforeDate比afterDate早的完整年数，负数表示晚。
     */
    public static int beforeRoundAges(final Date beforeDate, final Date afterDate) {
        Date bDate = beforeDate;
        Date aDate = afterDate;
        boolean positive = true;
        if (beforeDate.after(afterDate)) {
            positive = false;
            bDate = afterDate;
            aDate = beforeDate;
        }
        int beforeYears = beforeYears(bDate, aDate);

        int bMonth = getMonth(bDate);
        int aMonth = getMonth(aDate);
        if (aMonth < bMonth) {
            beforeYears--;
        }

        if (positive) {
            return beforeYears;
        } else {
            return new BigDecimal(beforeYears).negate().intValue();
        }
    }

    /**
     * 获取beforeDate和afterDate之间相差的完整月数，精确到天。负数表示晚。
     * 
     * @param beforeDate 要比较的早的日期
     * @param afterDate 要比较的晚的日期
     * @return beforeDate比afterDate早的完整月数，负数表示晚。
     */
    public static int beforeRoundMonths(final Date beforeDate, final Date afterDate) {
        Date bDate = beforeDate;
        Date aDate = afterDate;
        boolean positive = true;
        if (beforeDate.after(afterDate)) {
            positive = false;
            bDate = afterDate;
            aDate = beforeDate;
        }
        int beforeMonths = beforeMonths(bDate, aDate);

        int bDay = getDay(bDate);
        int aDay = getDay(aDate);
        if (aDay < bDay) {
            beforeMonths--;
        }

        if (positive) {
            return beforeMonths;
        } else {
            return new BigDecimal(beforeMonths).negate().intValue();
        }
    }

    /**
     * 根据传入的年、月、日构造日期对象
     * 
     * @param year 年
     * @param month 月
     * @param date 日
     * @return 返回根据传入的年、月、日构造的日期对象
     */
    public static Date getDate(final int year, final int month, final int date) {
        c.set(year + 1900, month, date);
        return c.getTime();
    }

    /**
     * 根据传入的日期格式化pattern将传入的日期格式化成字符串。
     * 
     * @param date 要格式化的日期对象
     * @param pattern 日期格式化pattern
     * @return 格式化后的日期字符串
     */
    public static String format(final Date date, final String pattern) {
    	if (date == null){
    		return null;
    	}
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    /**
     * 将传入的日期按照默认形势转换成字符串(yyyy-MM-dd)
     * 
     * @param date 要格式化的日期对象
     * @return 格式化后的日期字符串
     */
    public static String format(final Date date) {
        return format(date, YEAR_MONTH_DAY_PATTERN);
    }

    /**
     * 根据传入的日期格式化patter将传入的字符串转换成日期对象
     * 
     * @param dateStr 要转换的字符串
     * @param pattern 日期格式化pattern
     * @return 转换后的日期对象
     * @throws ParseException 如果传入的字符串格式不合法
     */
    public static Date parse(final String dateStr, final String pattern) throws ParseException {
    	if (StringUtils.isEmpty(dateStr)){
    		return null;
    	}
        DateFormat df = new SimpleDateFormat(pattern);
        return df.parse(dateStr);
    }

    /**
     * 将传入的字符串按照默认格式转换为日期对象(yyyy-MM-dd)
     * 
     * @param dateStr 要转换的字符串
     * @return 转换后的日期对象
     * @throws ParseException 如果传入的字符串格式不符合默认格式(如果传入的字符串格式不合法)
     */
    public static Date parse(final String dateStr) throws ParseException {
        return parse(dateStr, YEAR_MONTH_DAY_PATTERN);
    }

    /**
     * 要进行合法性验证的年月数值
     * 
     * @param yearMonth 验证年月数值
     * @return 年月是否合法
     */
    public static boolean isYearMonth(final Integer yearMonth) {
        String yearMonthStr = yearMonth.toString();
        return isYearMonth(yearMonthStr);
    }

    /**
     * 要进行合法性验证的年月字符串
     * 
     * @param yearMonthStr 验证年月字符串
     * @return 年月是否合法
     */
    public static boolean isYearMonth(final String yearMonthStr) {
        if (yearMonthStr.length() != 6) {
            return false;
        } else {
            String yearStr = yearMonthStr.substring(0, 4);
            String monthStr = yearMonthStr.substring(4, 6);
            try {
                int year = Integer.parseInt(yearStr);
                int month = Integer.parseInt(monthStr);
                if (year < 1800 || year > 3000) {
                    return false;
                }
                if (month < 1 || month > 12) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * 获取从from到to的年月Integer形式值的列表
     * 
     * @param from 从
     * @param to 到
     * @return 年月Integer形式值列表
     * @throws ParseException
     */
    public static List getYearMonths(Integer from, Integer to) throws ParseException {
        List yearMonths = new ArrayList();
        Date fromDate = parseYearMonth(from);
        Date toDate = parseYearMonth(to);
        if (fromDate.after(toDate)) {
            throw new IllegalArgumentException("'from' date should before 'to' date!");
        }
        Date tempDate = fromDate;
        while (tempDate.before(toDate)) {
            yearMonths.add(getYearMonth(tempDate));
            tempDate = addMonth(tempDate, 1);
        }
        if (!from.equals(to)) {
            yearMonths.add(to);
        }

        return yearMonths;
    }
    
    /**
     * 在日期时间上加上整数个小时
     * @param date 给定日期时间
     * @param ammount 小时数
     * @return 日期
     */
    public static Date addHour(final Date date, final int ammount) {
        c.setTime(date);
        c.add(Calendar.HOUR, ammount);
        return c.getTime();
    }
    
    public static long getBetweenIntervalCnt(Date d1, Date d2, int interval){
        Date startDate = d1;
        Date endDate = d2;
        if (d1.before(d2) == false){
            startDate = d2;
            endDate = d1;
        }
        
        long intervalDay = (endDate.getTime() - startDate.getTime()) / (1000*60*60*24);
        System.out.println("intervalDay:" + intervalDay);
        long intercalCnt = (intervalDay / interval) + 1;
        
        return intercalCnt;
    }


    public static Date truncate(Date date) {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            c.setTime(formatter.parse(formatter.format(date)));
            return c.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获得一天的最后时刻
     * @param date
     * @return
     */
    public static Date getLastMonmentOfDay(Date date) {
    	Calendar calendar=Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.set(Calendar.HOUR_OF_DAY, 23);
    	calendar.set(Calendar.MINUTE, 59);
    	calendar.set(Calendar.SECOND, 59);
    	return calendar.getTime();
    }


    /**
     * 获得一天的最后时刻
     * @param date
     * @return
     */
    public static Date getFirstMomentOfDay(Date date) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }
    
    public static Date beforeDate(Date date) {
		return beforeDate(date, 1);
	}

	public static Date beforeDate(Date date, int d) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, -d);
		return c.getTime();
	}
	
	
	public static Date max(Date... dates ){
		Date maxDate = dates[0];
		
		for(Date date : dates){
			if(date.after(maxDate)){
				maxDate = date;
			}
		}
		
		
		return maxDate;
	}
}
