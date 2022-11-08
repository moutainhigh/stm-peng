package com.mainsteam.stm.portal.netflow.bo.alarm;

public class AlarmThresholdBo {
	private String id;
	private String profileId;// 对应的profile表的ID，对应表中PROFILE_ID字段
	private String netflowAlarmThresholdMinute;// 对应时间，分钟数，对应表中的PERIOD字段
	private String netflowAlarmThresholdCount;// 对应出现次数，对应表中COUNT
	private String netflowAlarmThresholdValue;// 对应阀值，对应表中的VALUE
	private String netflowAlarmFlowUnit;// 对应单位，,但是在表中不对应任何单位
	private String netflowAlarmThresholdLevel;// 对应告警级别，对应呢表中的SEVERITY

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getNetflowAlarmThresholdMinute() {
		return netflowAlarmThresholdMinute;
	}

	public void setNetflowAlarmThresholdMinute(
			String netflowAlarmThresholdMinute) {
		this.netflowAlarmThresholdMinute = netflowAlarmThresholdMinute;
	}

	public String getNetflowAlarmThresholdCount() {
		return netflowAlarmThresholdCount;
	}

	public void setNetflowAlarmThresholdCount(String netflowAlarmThresholdCount) {
		this.netflowAlarmThresholdCount = netflowAlarmThresholdCount;
	}

	public String getNetflowAlarmThresholdValue() {
		return netflowAlarmThresholdValue;
	}

	public void setNetflowAlarmThresholdValue(String netflowAlarmThresholdValue) {
		this.netflowAlarmThresholdValue = netflowAlarmThresholdValue;
	}

	public String getNetflowAlarmFlowUnit() {
		return netflowAlarmFlowUnit;
	}

	public void setNetflowAlarmFlowUnit(String netflowAlarmFlowUnit) {
		this.netflowAlarmFlowUnit = netflowAlarmFlowUnit;
	}

	public String getNetflowAlarmThresholdLevel() {
		return netflowAlarmThresholdLevel;
	}

	public void setNetflowAlarmThresholdLevel(String netflowAlarmThresholdLevel) {
		this.netflowAlarmThresholdLevel = netflowAlarmThresholdLevel;
	}

}
