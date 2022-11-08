package com.mainsteam.stm.util;

import java.util.Calendar;
import java.util.Date;

public class NetFlowDataUtil {

	public static Date getStartMin() {
		return getStartMin(null);
	}

	public static Date getStartMin(Date date) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		int minute = cal.get(Calendar.MINUTE);
		for (int i = 0; i < 60; i += 10) {
			if (minute - i < 10) {
				cal.set(Calendar.MINUTE, i);
				break;
			}
		}
		return cal.getTime();
	}

	public static Date getStartHour(Date date) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date getStartHour() {
		return getStartHour(null);
	}

	public static Date getStartDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getStartHour(date));
		cal.set(Calendar.HOUR, 0);
		return cal.getTime();
	}

	public static Date getStartDay() {
		return getStartDay(null);
	}

	public static Date addMinute(Date date, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getStartHour(date));
		cal.add(Calendar.MINUTE, minute);
		return cal.getTime();
	}

	public static Date addHour(Date date, int hour) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getStartHour(date));
		cal.add(Calendar.HOUR, hour);
		return cal.getTime();
	}

	public static Date addDay(Date date, int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getStartHour(date));
		cal.add(Calendar.DATE, day);
		return cal.getTime();
	}
}
