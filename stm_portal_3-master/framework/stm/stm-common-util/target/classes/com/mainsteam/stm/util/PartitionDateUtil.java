package com.mainsteam.stm.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class PartitionDateUtil {

	private static final DateFormat partitionNameDateFormat = new SimpleDateFormat(DateUtil.format_yyyyMMdd);
	
	/**
	 * 转换成分区格式yyyyMMdd
	 * @param date
	 * @return
	 */
	public static String partitionNameFormat(Date date){
		return DateUtil.formatWithSimple(date,DateUtil.format_yyyyMMdd);
	}
	
	/**
	 * 转换成分区格式yyyy-MM-dd
	 * @param date
	 * @return
	 */
	public static String partitionDateStrFormat(Date date){
		//日期转换分区时间，需要添加1天
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, 1);
		return DateUtil.formatWithSimple(c.getTime(),DateUtil.DEFAULT_DATE_FORMAT);
	}
	
	/**
	 * 分区格式yyyyMMdd 转换成Date 对象
	 * @param date
	 * @return
	 */
	public static Date partitionDateFormat(String partionName){
		try {
			return partitionNameDateFormat.parse(partionName);
		} catch (ParseException e) {
		}
		return null;
	}
}
