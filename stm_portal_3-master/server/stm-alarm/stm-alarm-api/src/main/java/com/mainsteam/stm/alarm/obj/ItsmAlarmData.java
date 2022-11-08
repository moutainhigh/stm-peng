package com.mainsteam.stm.alarm.obj;

import java.util.Date;

public class ItsmAlarmData {
	private long alarmEventID;
	private String itsmOrderID;
	private ItsmOrderStateEnum state;
	private Date updateTime;
	
	
	public long getAlarmEventID() {
		return alarmEventID;
	}
	public void setAlarmEventID(long alarmEventID) {
		this.alarmEventID = alarmEventID;
	}
	public String getItsmOrderID() {
		return itsmOrderID;
	}
	public void setItsmOrderID(String itsmOrderID) {
		this.itsmOrderID = itsmOrderID;
	}
	public ItsmOrderStateEnum getState() {
		return state;
	}
	public void setState(ItsmOrderStateEnum state) {
		this.state = state;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
