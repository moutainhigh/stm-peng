package com.mainsteam.stm.portal.netflow.bo.alarm;
/*
 * 这个bo设计主要是用来在告警列表页面显示系统中所有的告警
 */
public class AlarmDataGridListBo {
	private Long id; // 告警记录的id
	private String netflowAlarmConfigName; //告警的名字
	private String netflowAlarmDesc; //告警的描述
	private String oneHoureAlarm; //告警一个小时内产生的告警
	private String allCountAlarm; //所有的告警
	private int state;//告警的当前状态
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNetflowAlarmConfigName() {
		return netflowAlarmConfigName;
	}
	public void setNetflowAlarmConfigName(String netflowAlarmConfigName) {
		this.netflowAlarmConfigName = netflowAlarmConfigName;
	}
	public String getNetflowAlarmDesc() {
		return netflowAlarmDesc;
	}
	public void setNetflowAlarmDesc(String netflowAlarmDesc) {
		this.netflowAlarmDesc = netflowAlarmDesc;
	}
	public String getOneHoureAlarm() {
		return oneHoureAlarm;
	}
	public void setOneHoureAlarm(String oneHoureAlarm) {
		this.oneHoureAlarm = oneHoureAlarm;
	}
	public String getAllCountAlarm() {
		return allCountAlarm;
	}
	public void setAllCountAlarm(String allCountAlarm) {
		this.allCountAlarm = allCountAlarm;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}

}
