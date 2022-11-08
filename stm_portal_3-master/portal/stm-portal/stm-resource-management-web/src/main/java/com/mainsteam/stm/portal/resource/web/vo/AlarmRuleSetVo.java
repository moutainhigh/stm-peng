package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;


public class AlarmRuleSetVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7850287701180769843L;
	
	private long alarmRulesID;
	private String sendWay;
	private String[] alarmLevel;
	private String sendCondition;
	private int continusCount;
	private String continusCountUnit;
	private String isAlarm;
	
	//allTime   everyDay  everyWeek
	private String alarmSendTimeType;
	//是否发送非告警时间告警
	private String ifSendUnalarmTimeAlarm;
	//周几-开始时间-结束时间          0表示每天          1-120-180(周一的2-3点)
	private String[] alarmForDayData;
	private String[] alarmForWeekData;
	
	private long  templateId;
	//最多可发送次数 dfw 20170104
	private int sendTimes;
	
	
	public String getAlarmSendTimeType() {
		return alarmSendTimeType;
	}
	public void setAlarmSendTimeType(String alarmSendTimeType) {
		this.alarmSendTimeType = alarmSendTimeType;
	}
	public String getIsAlarm() {
		return isAlarm;
	}
	public void setIsAlarm(String isAlarm) {
		this.isAlarm = isAlarm;
	}
	public String getIfSendUnalarmTimeAlarm() {
		return ifSendUnalarmTimeAlarm;
	}
	public void setIfSendUnalarmTimeAlarm(String ifSendUnalarmTimeAlarm) {
		this.ifSendUnalarmTimeAlarm = ifSendUnalarmTimeAlarm;
	}
	public String[] getAlarmForDayData() {
		return alarmForDayData;
	}
	public void setAlarmForDayData(String[] alarmForDayData) {
		this.alarmForDayData = alarmForDayData;
	}
	public String[] getAlarmForWeekData() {
		return alarmForWeekData;
	}
	public void setAlarmForWeekData(String[] alarmForWeekData) {
		this.alarmForWeekData = alarmForWeekData;
	}
	public long getAlarmRulesID() {
		return alarmRulesID;
	}
	public void setAlarmRulesID(long alarmRulesID) {
		this.alarmRulesID = alarmRulesID;
	}
	public String getSendWay() {
		return sendWay;
	}
	public void setSendWay(String sendWay) {
		this.sendWay = sendWay;
	}
	public String[] getAlarmLevel() {
		return alarmLevel;
	}
	public void setAlarmLevel(String[] alarmLevel) {
		this.alarmLevel = alarmLevel;
	}
	public String getSendCondition() {
		return sendCondition;
	}
	public void setSendCondition(String sendCondition) {
		this.sendCondition = sendCondition;
	}
	public int getContinusCount() {
		return continusCount;
	}
	public void setContinusCount(int continusCount) {
		this.continusCount = continusCount;
	}
	public String getContinusCountUnit() {
		return continusCountUnit;
	}
	public void setContinusCountUnit(String continusCountUnit) {
		this.continusCountUnit = continusCountUnit;
	}
	public long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	public int getSendTimes() {
		return sendTimes;
	}

	public void setSendTimes(int sendTimes) {
		this.sendTimes = sendTimes;
	}
}
