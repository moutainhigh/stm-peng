package com.mainsteam.stm.profilelib.alarm.obj;

import java.io.Serializable;
import java.util.List;

/**
 * 告警发送条件
 * 
 * @author ziw
 * 
 */
public class AlarmSendCondition implements Serializable {

	/***/
	private static final long serialVersionUID = 8778127799839273757L;
	
	private long ID;

	/**告警发送级别的key列表 */
	private List<AlarmLevelEnum> alarmLevels;

	/**告警发送方式	 */
	private SendWayEnum sendWay;

	/**
	 * 判断是否启动在一定时间段内，只发送一条告警
	 */
	private boolean continus;

	/**
	 * 连续时间长度
	 */
	private int continusCount;

	/**
	 * 连续时间长度单位
	 */
	private ContinusUnitEnum continusCountUnit;

	/**是否为全天*/
	private boolean allTime;
	/**非告警时间产生告警，是否发送*/
	private boolean sendIntime;
	/**每天的时间段*/
	private List<AlarmNotifyPeriodForDay> dayPeriodes;
	/**每周某天的时间段*/
	private List<AlarmNotifyPeriodForWeek> weekPeriodes;
	
	/**是否启用该告警发送条件 */
	private boolean enabled;
	/**
	 * 告警通知短信及邮件模板
	 */
	private long templateId;
	/**
	 * 告警规则Id
	 */
	private long ruleId;
	/**
	 * 在指定的时间内发送次数;
	 */
	private int sendTimes;

	public boolean isAllTime() {
		return allTime;
	}

	public void setAllTime(boolean allTime) {
		this.allTime = allTime;
	}

	public boolean isSendIntime() {
		return sendIntime;
	}

	public void setSendIntime(boolean sendIntime) {
		this.sendIntime = sendIntime;
	}

	public List<AlarmNotifyPeriodForDay> getDayPeriodes() {
		return dayPeriodes;
	}

	public void setDayPeriodes(List<AlarmNotifyPeriodForDay> dayPeriodes) {
		this.dayPeriodes = dayPeriodes;
	}

	public List<AlarmNotifyPeriodForWeek> getWeekPeriodes() {
		return weekPeriodes;
	}

	public void setWeekPeriodes(List<AlarmNotifyPeriodForWeek> weekPeriodes) {
		this.weekPeriodes = weekPeriodes;
	}

	
	/**
	 * @return the alarmLevels
	 */
	public final List<AlarmLevelEnum> getAlarmLevels() {
		return alarmLevels;
	}

	/**
	 * @param alarmLevels the alarmLevels to set
	 */
	public final void setAlarmLevels(List<AlarmLevelEnum> alarmLevels) {
		this.alarmLevels = alarmLevels;
	}

	/**
	 * @return the sendWay
	 */
	public final SendWayEnum getSendWay() {
		return sendWay;
	}

	/**
	 * @param sendWay the sendWay to set
	 */
	public final void setSendWay(SendWayEnum sendWay) {
		this.sendWay = sendWay;
	}

	/**
	 * @return the continus
	 */
	public final boolean isContinus() {
		return continus;
	}

	/**
	 * @param continus the continus to set
	 */
	public final void setContinus(boolean continus) {
		this.continus = continus;
	}

	/**
	 * @return the continusCount
	 */
	public final int getContinusCount() {
		return continusCount;
	}

	/**
	 * @param continusCount the continusCount to set
	 */
	public final void setContinusCount(int continusCount) {
		this.continusCount = continusCount;
	}

	/**
	 * @return the continusCountUnit
	 */
	public final ContinusUnitEnum getContinusCountUnit() {
		return continusCountUnit;
	}

	/**
	 * @param continusCountUnit the continusCountUnit to set
	 */
	public final void setContinusCountUnit(ContinusUnitEnum continusCountUnit) {
		this.continusCountUnit = continusCountUnit;
	}

	/**
	 * @return the enabled
	 */
	public final boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	public long getRuleId() {
		return ruleId;
	}

	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}

	public int getSendTimes() {
		return sendTimes;
	}

	public void setSendTimes(int sendTimes) {
		this.sendTimes = sendTimes;
	}

}
