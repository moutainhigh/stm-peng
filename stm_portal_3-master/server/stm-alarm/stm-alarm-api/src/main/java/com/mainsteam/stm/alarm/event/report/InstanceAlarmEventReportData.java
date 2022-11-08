package com.mainsteam.stm.alarm.event.report;


public class InstanceAlarmEventReportData {
	private int countCritical;
	private int countSerious;
	private int countWarn;
	private int countUnkown;
	private int countNormal;
	private int countTotle;
	private int countRecover;
	private int countNotRecover;
	private long instanceID;
	
	
	public int getCountCritical() {
		return countCritical;
	}
	public void setCountCritical(int countCritical) {
		this.countCritical = countCritical;
	}
	public int getCountSerious() {
		return countSerious;
	}
	public void setCountSerious(int countSerious) {
		this.countSerious = countSerious;
	}
	public int getCountWarn() {
		return countWarn;
	}
	public void setCountWarn(int countWarn) {
		this.countWarn = countWarn;
	}
	public int getCountUnkown() {
		return countUnkown;
	}
	public void setCountUnkown(int countUnkown) {
		this.countUnkown = countUnkown;
	}
	public int getCountNormal() {
		return countNormal;
	}
	public void setCountNormal(int countNormal) {
		this.countNormal = countNormal;
	}
	public long getInstanceID() {
		return instanceID;
	}
	public void setInstanceID(long instanceID) {
		this.instanceID = instanceID;
	}
	public int getCountRecover() {
		return countRecover;
	}
	public void setCountRecover(int countRecover) {
		this.countRecover = countRecover;
	}
	public int getCountNotRecover() {
		return countNotRecover;
	}
	public void setCountNotRecover(int countNotRecover) {
		this.countNotRecover = countNotRecover;
	}
	public int getCountTotle() {
		return countTotle;
	}
	public void setCountTotle(int countTotle) {
		this.countTotle = countTotle;
	}
}
