package com.mainsteam.stm.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <li>文件名称: DateUtil.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月14日
 * @author   ziwenwen
 */
public class DateUtil {
	
	private static final Log logger = LogFactory.getLog(DateUtil.class);

	/**
	 * 默认日期格式
	 */
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	/**
	 * 默认日期+时间格式
	 */
	public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	private static final DateFormat parseDate = new SimpleDateFormat(DEFAULT_DATE_FORMAT);

	private static final DateFormat parseDateTime = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);

	public static final String format_yyyyMMdd="yyyyMMdd";
	public static final String format_yyyyMM="yyyyMM";
	public static final String format_yyyy="yyyy";
	public static final String format_yyyycnMMcnddcn="yyyy年MM月dd日";
	public static final String format_yyyycnMMcn="yyyy年MM月";
	public static final String format_yyyycn="yyyy年";
	
	/**
	 * 不允许创建该类实例
	 */
	private DateUtil(){};
	/**
	 * 根据给定日期给出一个Calendar对象
	 * @param date
	 * @return
	 */
	public static Calendar getCalendar(Date date){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
	
	/**
	 * 重设一个日期的时分秒时间
	 * @param date
	 * @param hour
	 * @param minutes
	 * @param seconds
	 * @return
	 */
	public static Date reset(Calendar calendar,int hour,int minutes,int seconds){
		calendar.set(Calendar.HOUR_OF_DAY,hour);
		calendar.set(Calendar.MINUTE,minutes);
		calendar.set(Calendar.SECOND,seconds);
		return calendar.getTime();
	}
	
	/**
	 * 方法用于将日期值转换为字符串YYYY-MM-DD HH:MM:SS
	 * @return
	 */
	public static String format(Date date){
		return DateFormat.getDateTimeInstance(2,2).format(date);
	}
	
	/**
	 * 根据给定的格式格式化日期(使用<b>SimpleDateFormat</b>)
	 * @param Calendar 要格式的日期 
	 * @param format 格式后的格式<br/>
	 * 字母  日期或时间元素  表示  示例   <br/>
	 *	G  Era 标志符  Text  AD   <br/>
	 *	y  年  Year  1996; 96   <br/>
	 *	M  年中的月份  Month  July; Jul; 07   <br/>
	 *	w  年中的周数  Number  27   <br/>
	 *	W  月份中的周数  Number  2   <br/>
	 *	D  年中的天数  Number  189   <br/>
	 *	d  月份中的天数  Number  10   <br/>
	 *	F  月份中的星期  Number  2   <br/>
	 *	E  星期中的天数  Text  Tuesday; Tue   <br/>
	 *	a  Am/pm 标记  Text  PM   <br/>
	 *	H  一天中的小时数（0-23）  Number  0   <br/>
	 *	k  一天中的小时数（1-24）  Number  24   <br/>
	 *	K  am/pm 中的小时数（0-11）  Number  0   <br/>
	 *	h  am/pm 中的小时数（1-12）  Number  12   <br/>
	 *	m  小时中的分钟数  Number  30   <br/>
	 *	s  分钟中的秒数  Number  55   <br/>
	 *	S  毫秒数  Number  978   <br/>
	 *	z  时区  General time zone  Pacific Standard Time; PST; GMT-08:00   <br/>
	 *	Z  时区  RFC 822 time zone  -0800  
	 * @param date
	 * @param format
	 * @return
	 */
	public static String formatWithSimple(Date date,String format){
		return new SimpleDateFormat(format).format(date);
	}
	
	/**
	 * 根据给定的格式格式化日期
	 * @param Calendar 要格式的日期 
	 * @param format 格式后的格式<br/>
	 * 字母  日期或时间元素 <br/>
	 *	y  年  Year<br/>
	 *	M  年中的月份 <br/>
	 *	d  月份中的天数 <br/>
	 *	H  一天中的小时数（0-23）<br/>
	 *	h  am/pm 中的小时数（1-12）<br/>
	 *	m  小时中的分钟数  Number <br/>
	 *	s  分钟中的秒数  Number<br/>
	 *	S  毫秒数  Number <br/>
	 * @see #formatDate(Calendar, String)
	 * @return String 格式后的字符串
	 */
	public static String format(Date date,String format){
		return format(getCalendar(date), format);
	}
	
	/**
	 * 根据给定的格式格式化日期
	 * @param Calendar 要格式的日期 
	 * @param format 格式后的格式<br/>
	 * 字母  日期或时间元素 <br/>
	 *	y  年  Year<br/>
	 *	M  年中的月份 <br/>
	 *	d  月份中的天数 <br/>
	 *	H  一天中的小时数（0-23）<br/>
	 *	h  am/pm 中的小时数（1-12）<br/>
	 *	m  小时中的分钟数  Number <br/>
	 *	s  分钟中的秒数  Number<br/>
	 *	S  毫秒数  Number <br/>
	 * @return String 格式后的字符串
	 */
	public static String format(Calendar calendar,String format){
		int year=calendar.get(Calendar.YEAR);
		format=format.replaceAll("yyyy",String.valueOf(year));
		format=format.replaceAll("yy",String.valueOf(year%100));
		String month=String.valueOf(calendar.get(Calendar.MONTH)+1);//默认从0开始，加一进行修正
		format=format.replaceAll("MM",month.length()==1?'0'+month:month);
		format=format.replaceAll("M",month);
		String date1=String.valueOf(calendar.get(Calendar.DATE));
		format=format.replaceAll("dd",date1.length()==1?'0'+date1:date1);
		format=format.replaceAll("d",date1);
		int hour=calendar.get(Calendar.HOUR_OF_DAY);
		String hour1=String.valueOf(hour);
		format=format.replaceAll("HH",hour1.length()==1?'0'+hour1:hour1);
		format=format.replaceAll("H",hour1);
		if(hour>12)hour-=12;
		hour1=String.valueOf(hour);
		format=format.replaceAll("hh",hour1.length()==1?'0'+hour1:hour1);
		format=format.replaceAll("h",hour1);
		String minute=String.valueOf(calendar.get(Calendar.MINUTE));
		format=format.replaceAll("mm",minute.length()==1?'0'+minute:minute);
		format=format.replaceAll("m",minute);
		String second=String.valueOf(calendar.get(Calendar.SECOND));
		format=format.replaceAll("ss",second.length()==1?'0'+second:second);
		format=format.replaceAll("s",second);
		format=format.replaceAll("[SSSS|SSS|SS|S]",String.valueOf(calendar.get(Calendar.MILLISECOND)));
		return format;
	}

	/**
	 * 解析日期 yyyy-MM-dd
	 * @param date
	 * @return
	 */
	public static Date parseDate(String date){
		try {
			return parseDate.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 解析日期时间 yyyy-MM-dd HH:mm:ss
	 * @param datetime
	 * @return
	 */
	public static Date parseDateTime(String datetime){
		try {
			return parseDateTime.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据时间对象获取所属日期的开始、结束时间（单位秒）
	 * @param calendar
	 */
	public static int[] parseBeginEndDateTime(Calendar calendar){
		int[] times=new int[2];
		Calendar c=Calendar.getInstance();
		c.setTime(calendar.getTime());
		DateUtil.reset(c,0,0,0);
		times[0]=(int)(c.getTime().getTime()/1000);
		DateUtil.reset(c,23,59,59);
		times[1]=(int)(c.getTime().getTime()/1000);
		return times;
	}
	
	/**
	 * 根据时间对象获取所属月份的开始、结束时间（单位秒）
	 * @param calendar
	 */
	public static int[] parseBeginEndMonthTime(Calendar calendar){
		Calendar c=Calendar.getInstance();
		c.setTime(calendar.getTime());
		DateUtil.reset(c,0,0,0);
		int[] times=new int[2];
		
		c.set(Calendar.DAY_OF_MONTH,1);
		times[0]=(int)(c.getTime().getTime()/1000);

		c.set(Calendar.MONTH,c.get(Calendar.MONTH)+1);
		times[1]=(int)(c.getTime().getTime()/1000);
		return times;
	}
	
	/**
	 * 根据时间对象获取所属年份的开始、结束时间（单位秒）
	 * @param calendar
	 */
	public static int[] parseBeginEndYearTime(Calendar calendar){
		Calendar c=Calendar.getInstance();
		int[] times=new int[2];
		
		c.set(c.get(Calendar.YEAR),0,1,0,0,0);
		times[0]=(int)(c.getTime().getTime()/1000);
		
		c.set(c.get(Calendar.YEAR)+1,0,0,23,59,59);
		times[1]=(int)(c.getTime().getTime()/1000);
		
		return times;
	}
	/**
	 * 格式化指定格式的日期字符串
	 * @param dateStr	日期字符串
	 * @param pattern 日期格式
	 * @return
	 * @author  ziwen
	 * @date	2019年8月18日
	 */
	public static Date parseDate(String dateStr, String pattern) {
		DateFormat dateFormat = new SimpleDateFormat(pattern);
		try {
			return dateFormat.parse(dateStr);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("DateUtil.parseDate : ", e);
			}
			return null;
		}
	}
	
	/**
	 * 添加天数
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date addHour(Date date,int hour){
	   Calendar calendar=Calendar.getInstance();   
	   calendar.setTime(date); 
	   calendar.set(Calendar.HOUR_OF_DAY,calendar.get(Calendar.HOUR_OF_DAY)+hour);
	   return calendar.getTime();  
	   
	}
	
	/**
	 * 减去天数
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date subHour(Date date,int hour){
	   Calendar calendar=Calendar.getInstance();   
	   calendar.setTime(date); 
	   calendar.set(Calendar.HOUR_OF_DAY,calendar.get(Calendar.HOUR_OF_DAY)-hour);
	   return calendar.getTime(); 
	}
	
	/**
	 * 添加小时
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date addDay(Date date,int day){
	   Calendar calendar=Calendar.getInstance();   
	   calendar.setTime(date); 
	   calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)+day);
	   return calendar.getTime();  
	}
	
	/**
	 * 减去小时
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date subDay(Date date,int day){
	   Calendar calendar=Calendar.getInstance();   
	   calendar.setTime(date); 
	   calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)-day);
	   return calendar.getTime(); 
	}
	
}


