package com.mainsteam.stm.profilelib.alarm.obj;

public class AlarmNotifyPeriodForWeek extends AlarmNotifyPeriodForDay{
	private int dayOfWeek;

	public int getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
}
