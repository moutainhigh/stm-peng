/**
 * 
 */
package com.mainsteam.stm.profilelib.po;


/**
 * 对应告警规则条件表
 * 
 * @author ziw
 * 
 */
public class AlarmRuleConditionPO {

	/**
	 * 告警规则id
	 */
	private long ruleId = -1;
	
	private long conditionId = -1;

	/**
	 * 告警规则发送方式
	 */
	private String sendWay;

	/**
	 * 告警规则是否启用
	 */
	private int enabled = -1;

	/**
	 * 告警规则里的连续发送是否启用
	 */
	private int continus = -1;

	/**
	 * 告警规则里的连续发送时间间隔长度
	 */
	private int continusCount = -1;

	/**
	 * 告警规则里的连续发送时间间隔长度的单位
	 */
	private String continusUnit;

	/**
	 * 告警发送级别列表
	 */
	private String alarmLevels;
	
	/**是否为全天*/
	private boolean allTime;
	/**非告警时间产生告警，是否发送*/
	private boolean sendIntime;

	/**每天的时间段*/
	private String dayPeriodes;
	/**每周某天的时间段*/
	private String weekPeriodes;
	/**
	 * 告警邮件短信模板ID
	 */
	private long templateId;
	/**
	 * 在指定的时间内发送次数
	 */
	private int sendTimes;

	public String getDayPeriodes() {
		return dayPeriodes;
	}

	public void setDayPeriodes(String dayPeriodes) {
		this.dayPeriodes = dayPeriodes;
	}

	public String getWeekPeriodes() {
		return weekPeriodes;
	}

	public void setWeekPeriodes(String weekPeriodes) {
		this.weekPeriodes = weekPeriodes;
	}

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

	/**
	 * @return the ruleId
	 */
	public final long getRuleId() {
		return ruleId;
	}

	/**
	 * @param ruleId the ruleId to set
	 */
	public final void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}

	/**
	 * @return the sendWay
	 */
	public final String getSendWay() {
		return sendWay;
	}

	/**
	 * @param sendWay the sendWay to set
	 */
	public final void setSendWay(String sendWay) {
		this.sendWay = sendWay;
	}

	/**
	 * @return the enabled
	 */
	public final int getEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public final void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the continus
	 */
	public final int getContinus() {
		return continus;
	}

	/**
	 * @param continus the continus to set
	 */
	public final void setContinus(int continus) {
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
	 * @return the continusUnit
	 */
	public final String getContinusUnit() {
		return continusUnit;
	}

	/**
	 * @param continusUnit the continusUnit to set
	 */
	public final void setContinusUnit(String continusUnit) {
		this.continusUnit = continusUnit;
	}

	/**
	 * @return the alarmLevels
	 */
	public final String getAlarmLevels() {
		return alarmLevels;
	}

	/**
	 * @param alarmLevels the alarmLevels to set
	 */
	public final void setAlarmLevels(String alarmLevels) {
		this.alarmLevels = alarmLevels;
	}

	public long getConditionId() {
		return conditionId;
	}

	public void setConditionId(long conditionId) {
		this.conditionId = conditionId;
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
