/**
 * 
 */
package com.mainsteam.stm.portal.netflow.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * <li>文件名称: TimeUtil.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月27日
 * @author   lil
 */
public class TimeUtil {
	
	//流量表类型：10分钟表，2小时表，1天表等
	private static final String	TABLE_1MIN	= "1MIN";
	private static final String	TABLE_10MIN	= "10MIN";
	private static final String	TABLE_2HOUR	= "2HOUR";
	private static final String	TABLE_1DAY	= "1DAY";
	private static final String	TABLE_1WEEK	= "1WEEK";
	private static final String	TABLE_1MON	= "1MON";
	
	//根据时间段，查询表的规则
	private static final long RULE_10MIN	= 1000*60*60*24L;    //24小时内查询10分钟的表
	private static final long RULE_2HOUR	= 1000*60*60*24*7L;  //24小时以上，7天以内查询2小时的表
	private static final long RULE_1DAY		= 1000*60*60*24*90L; //7天以上，3个月以内查询1天的表
	
	//各种表的数据保存的时间
	private static final long KEEP_10MIN	= 1000*60*60*24*8L;	//10分钟的表最大保存8天
	private static final long KEEP_2HOUR	= 1000*60*60*24*70L; //2小时的表最大保存10周
	private static final long KEEP_1DAY		= 1000*60*60*24*390L; //1天的表最大保存13个月	

	private static Map<String, Long>	timeUnit	= new HashMap<String, Long>();
	static {
		timeUnit.put(TABLE_1MIN, (long) (60 * 1000 ));
		timeUnit.put(TABLE_10MIN, (long) (60 * 1000 * 10));
		timeUnit.put(TABLE_2HOUR, (long) (60 * 1000 * 60 * 2));
		timeUnit.put(TABLE_1DAY, (long) (60 * 1000 * 60 * 24));
		timeUnit.put(TABLE_1WEEK, (long) (60 * 1000 * 60 * 24 * 7));
		timeUnit.put(TABLE_1MON, ((long) (60 * 1000 * 60 * 24)) * 30);
	}
	
	/**
	 * 
	 * 根据起止时间与流量表类型，生成该段时间内的时间片
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param tableTimePostfix 表类型
	 * @return 时间片的list
	 */
	public static List<Long> getTimePoint(String startTime, String endTime, String  tableTimePostfix) {
		List<Long> timePointList = new ArrayList<Long>();
		long mills_unit = timeUnit.get(tableTimePostfix);
		
		Calendar startCalendar = Calendar.getInstance(TimeZone.getTimeZone("Africa/Casablanca"));
		Calendar endCalendar = Calendar.getInstance(TimeZone.getTimeZone("Africa/Casablanca"));

		try {
			startCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime.trim()));
			endCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime.trim()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		long startLong = startCalendar.getTimeInMillis();
		long endLong = endCalendar.getTimeInMillis();

		Calendar nowCalendar = Calendar.getInstance();
		nowCalendar.setTime(new Date());
		long nowTimeLong = nowCalendar.getTimeInMillis();
		if (endLong > nowTimeLong) {
			endLong = nowTimeLong;
		}
		startLong += TimeZone.getDefault().getRawOffset();
		startLong = startLong - (startLong % mills_unit);
		startLong -= TimeZone.getDefault().getRawOffset();
		
		endLong += TimeZone.getDefault().getRawOffset();
		endLong = endLong - (endLong % mills_unit);
		endLong -= TimeZone.getDefault().getRawOffset();
		
		int count = (int) ((endLong - startLong) / mills_unit) ;
		for (int i = 0; i <= count; i++) {
			timePointList.add(startLong + i * mills_unit);
		}
		return timePointList;
	}
	
	/** 
	 * 根据起止时间返回时间段内的如：20:05,20:10,。。。格式的集合
	 * 
	 * @param startTime
	 * @param endTime
	 * @param tableTimePostfix
	 * @return
	 * List<String>    返回类型 
	 */
	public static List<String> getTimeHm(String startTime, String endTime, String  tableTimePostfix) {
		List<Long> longList = getTimePoint(startTime, endTime, tableTimePostfix);
		List<String> hmList = new ArrayList<String>();
		if(null != longList && !longList.isEmpty()) {
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			for(long d : longList) {
				String ret = f.format(new Date(d));
				hmList.add(ret);
			}
		}
		return hmList;
	}
	
	public static List<String> getTimeLineList(List<Long> longList, String tableSuffix) {
		List<String> hmList = new ArrayList<String>();
		if(null != longList && !longList.isEmpty()) {
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			for(long d : longList) {
				String ret = f.format(new Date(d));
				hmList.add(ret);
			}
		}
		return hmList;
	}
	
	/**
	 * 
	 * 根据时间段，获取起止时间  
	 * 
	 */
	public static String getTimeFromPeroid(String period) {

		Calendar now = Calendar.getInstance();
		int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);
		int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int minute = now.get(Calendar.MINUTE);
		int second = now.get(Calendar.SECOND);
		String startTime = null;
		String endTime = null;
		DateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if("1hour".equals(period)){
			minute = minute%10;
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.HOUR_OF_DAY, -1);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		}else if("6hour".equals(period)){
			minute = minute%10;
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.HOUR_OF_DAY, -6);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		}else if("1day".equals(period)){
			minute = minute%10;
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_MONTH, -1);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		}else if("7day".equals(period)){
			hour = hour%2;
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_MONTH, -7);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		}else if("30day".equals(period)){
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_MONTH, -30);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		}else if("90day".equals(period)){
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_YEAR, -90);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		}else if("yesterday".equals(period)){
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_MONTH, -1);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		}else if("lastweek".equals(period)){
			now.add(Calendar.DAY_OF_MONTH, -(dayOfWeek-2));
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_MONTH, -7);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		}else if("lastmonth".equals(period)){
			now.add(Calendar.DAY_OF_MONTH, -(dayOfMonth-1));
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_MONTH, -1);
			int lastMonth = now.get(Calendar.DAY_OF_MONTH);
			now.add(Calendar.DAY_OF_MONTH, -(lastMonth-1));
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		}else if("today".equals(period)){
			int minutes = minute%10;
			now.add(Calendar.MINUTE, -minutes);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -(minute-minutes));
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		}else if("currweek".equals(period)){
			if(dayOfWeek>2){
				int hours = hour%2;
				now.add(Calendar.HOUR, -hours);
				now.add(Calendar.MINUTE, -minute);
				now.add(Calendar.SECOND, -second);
				endTime = dtf.format(new Date(now.getTimeInMillis()));
				now.add(Calendar.HOUR, -(hour-hours));
				now.add(Calendar.DAY_OF_MONTH, -(dayOfWeek-2));
				startTime = dtf.format(new Date(now.getTimeInMillis()));
			}else{
				int minutes = minute%10;
				now.add(Calendar.MINUTE, -minutes);
				now.add(Calendar.SECOND, -second);
				endTime = dtf.format(new Date(now.getTimeInMillis()));
				now.add(Calendar.DAY_OF_MONTH, -(dayOfWeek-2));
				now.add(Calendar.HOUR, -hour);
				now.add(Calendar.MINUTE, -(minute-minutes));
				startTime = dtf.format(new Date(now.getTimeInMillis()));
			}
		}else if("currmonth".equals(period)){
			if(dayOfMonth==1){
				int minutes = minute%10;
				now.add(Calendar.MINUTE, -minutes);
				now.add(Calendar.SECOND, -second);
				endTime = dtf.format(new Date(now.getTimeInMillis()));
				now.add(Calendar.HOUR, -hour);
				now.add(Calendar.MINUTE, -(minute-minutes));
				startTime = dtf.format(new Date(now.getTimeInMillis()));
			}else if(dayOfMonth>1 && dayOfMonth<=7){
				int hours = hour%2;
				now.add(Calendar.HOUR, -hours);
				now.add(Calendar.MINUTE, -minute);
				now.add(Calendar.SECOND, -second);
				endTime = dtf.format(new Date(now.getTimeInMillis()));
				now.add(Calendar.HOUR, -(hour-hours));
				now.add(Calendar.DAY_OF_MONTH, -(dayOfMonth-1));
				startTime = dtf.format(new Date(now.getTimeInMillis()));
				startTime = dtf.format(new Date(now.getTimeInMillis()));
			}else if(dayOfMonth>7){
				now.add(Calendar.HOUR, -hour);
				now.add(Calendar.MINUTE, -minute);
				now.add(Calendar.SECOND, -second);
				endTime = dtf.format(new Date(now.getTimeInMillis()));
				now.add(Calendar.DAY_OF_MONTH, -(dayOfMonth-1));
				startTime = dtf.format(new Date(now.getTimeInMillis()));
			}
		}else{
			return "custom";
		}
		return startTime + "|" + endTime;
	}
	
	/*
	 * 根据时间段，当前时间，获取起始时间
	 */
	public static String getStartTimeByPeroid(int period, Calendar now) {

		String startTime = null;
		DateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		switch (period) {
		case 2:
			now.add(Calendar.MINUTE, -2 * 60);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
			return startTime;
		case 6:
			now.add(Calendar.MINUTE, -6 * 60);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
			return startTime;
		case 24:
			now.add(Calendar.MINUTE, -24 * 60);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
			return startTime;
		case 10:
			startTime = df.format(new Date(now.getTimeInMillis())) + " 00:00:00";
			return startTime;
		case 20:
			now.add(Calendar.DAY_OF_MONTH, -1);
			startTime = df.format(new Date(now.getTimeInMillis())) + " 00:00:00";
			return startTime;
		}
		return null;
	}
	
	/**
	 * 
	 * getQueryDate 
	 * @param startTime
	 * @param endTime
	 * @return
	 * long[]    返回类型
	 */
	public static long[] getQueryDate(String startTime, String endTime) {
		long stime = 0;
		long etime = 0;
		SimpleDateFormat df_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			stime = df_time.parse(startTime).getTime() / 1000;
			etime = df_time.parse(endTime).getTime() / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new long[]{stime, etime};
	}
	
	
	/** 
	 * 根据起止时间得到时间差
	 * @param startTime
	 * @param endTime
	 * @return
	 * long    返回类型 
	 */
	public static long getReduceTime(String startTime, String endTime) {
		long stime = 0;
		long etime = 0;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			stime = df.parse(startTime).getTime();
			etime = df.parse(endTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return etime - stime;
	}
	
	/** 
	 * 获取本周第一天
	 * @return
	 * String    返回类型 
	 */
	public static String getFirstDayOfWeek() {
		int mondayPlus;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cd = Calendar.getInstance();
		int d = cd.get(Calendar.DAY_OF_WEEK)-1;//国外周日是每周的第一天，我需要每周一才是第一天
		if(d==1){
			mondayPlus = 0;
		}else{
			mondayPlus = 1-d;
		}
		cd.add(Calendar.DAY_OF_WEEK, mondayPlus);
		String firstDay = df.format(cd.getTime());
		return firstDay+" 00:00:00";
	}
	
	/** 
	 * 获取本月第一天
	 * @return
	 * String    返回类型 
	 */
	public static String getFirstDayOfMonth() {
		int mondayPlus;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cd = Calendar.getInstance();
		int d = cd.get(Calendar.DAY_OF_MONTH);
		if(d==1){
			mondayPlus = 0;
		}else{
			mondayPlus = 1-d;
		}
		cd.add(Calendar.DAY_OF_MONTH, mondayPlus);
		String firstDay = df.format(cd.getTime());
		return firstDay+" 00:00:00";
	}
	
	
	/** 
	 * 根据起止时间获取表明的时间部分。
	 * <br>如：
	 * 	<br>如果时间差小于24小时，查询10分钟表
	 * 	<br>如果时间差大于24小时，查询2h表等
	 * 	<br>如果时间差小于一周，查询2h表
	 * 	<br>如果时间差大于1周，查询1day表
	 * @param fromTime 开始时间
	 * @param toTime 结束时间
	 * @return 表的时间部分：10MIN,2HOUR,1DAY
	 * String    返回类型 
	 */
	public static String getTableSuffix(String fromTime,String toTime){
		String timepart = "10MIN";
		long chayi = TimeUtil.getReduceTime(fromTime, toTime);
		String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		fromTime = fromTime.split(" ")[0]+" "+"00:00:00";
		long chayiFromNow = TimeUtil.getReduceTime(fromTime, nowTime);
		if(chayiFromNow >=0 && chayiFromNow<= KEEP_10MIN){ //10分钟的表，数据保存时间为24-48小时
			if (chayi >=0 && chayi <= RULE_10MIN) {//小于24小时的都用10min的表
				timepart = TABLE_10MIN;
			}else if (chayi > RULE_10MIN && chayi <= KEEP_10MIN) {//大于24小时，查2h表
				timepart = TABLE_2HOUR;
			}
		}else if(chayiFromNow >KEEP_10MIN && chayiFromNow<= KEEP_2HOUR){//2小时的表，数据保存时间为7-14天
			if (chayi >=0 && chayi <= RULE_2HOUR) {//小于1周，查2小时的表
				timepart = TABLE_2HOUR;
			}else if (chayi > RULE_2HOUR && chayi <= KEEP_2HOUR) {//大于1周，查1day表
				timepart = TABLE_1DAY;
			}
		}else{
			timepart = TABLE_1DAY;
		}
		return timepart;
		
		
	}
	
	
	/** 
	 * 根据表名的时间部分，获取计算速率时最大，最小，平均速率应该除以的时间值
	 * 
	 * @param timepart
	 * @return
	 * long    返回类型 
	 */
	public static long getTimeByNames(String timepart) {
		return timeUnit.get(timepart)/1000;
	}
	
	
	/** 
	 * 将Long类型的时间转换成String类型的自然时间
	 * 
	 * @param List<Long> timePoints
	 * @return
	 * List<String>    返回类型 
	 */
	public static List<String> getRealTimePoint(List<Long> timePoint){
			List<String> timepoint = new ArrayList<String>();
			for(Long time : timePoint){
				Date d = new Date(time);
				String strtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
				timepoint.add(strtime);
			}
			return timepoint;
	}
	
	/** 
	 * 根据起止时间获取表明的时间部分。
	 * <br>如：
	 * 	<br>如果时间差小于24小时，查询10分钟表
	 * 	<br>如果时间差大于24小时，查询2h表等
	 * 	<br>如果时间差小于一周，查询2h表
	 * 	<br>如果时间差大于1周，查询1day表
	 * @param fromTime 开始时间
	 * @param toTime 结束时间
	 * @return 表的时间部分：10MIN,2HOUR,1DAY
	 * String    返回类型 
	 */
	public static String getTimeOfTables(String fromTime,String toTime){
		String timepart = "";
		long chayi = TimeUtil.getReduceTime(fromTime, toTime);
		String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		fromTime = fromTime.split(" ")[0]+" "+"00:00:00";
		long chayiFromNow = TimeUtil.getReduceTime(fromTime, nowTime);
		if(chayiFromNow >=0 && chayiFromNow<= KEEP_10MIN){ //10分钟的表，数据保存时间为24-48小时
			if (chayi >=0 && chayi <= RULE_10MIN) {//小于24小时的都用10min的表
				timepart = TABLE_10MIN;
			}else if (chayi > RULE_10MIN && chayi <= KEEP_10MIN) {//大于24小时，查2h表
				timepart = TABLE_2HOUR;
			}
		}else if(chayiFromNow >KEEP_10MIN && chayiFromNow<= KEEP_2HOUR){//2小时的表，数据保存时间为7-14天
			if (chayi >=0 && chayi <= RULE_2HOUR) {//小于1周，查2小时的表
				timepart = TABLE_2HOUR;
			}else if (chayi > RULE_2HOUR && chayi <= KEEP_2HOUR) {//大于1周，查1day表
				timepart = TABLE_1DAY;
			}
		}else{
			timepart = TABLE_1DAY;
		}
		return timepart;
		
	}
	
	/**
	 * 得到时间除以1000后的集合，数据库使用该时间
	 * 
	 * @param src
	 * @return
	 * List<Long>    返回类型
	 */
	public static List<Long> getTimePointDevide1000(List<Long> src) {
		List<Long> dst = new ArrayList<Long>();
		if(null != src && !src.isEmpty()) {
			for(long s : src) {
				dst.add(s / 1000);
			}
		}
		return dst;
	}
	
	/**
	 * 将时间组成逗号分隔的串用于查询
	 * 
	 * @param src
	 * @return
	 * String    返回类型
	 */
	public static String getTimeString(List<Long> src) {
		StringBuffer sb = new StringBuffer();
		if(null != src && !src.isEmpty()) {
			for(long d : src) {
				sb.append(d).append(",");
			}
			sb = sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}

}
