package com.mainsteam.stm.profilelib.po;

public class ProfileThresholdPO {

	private long mkId;
	private long profileId;
	private String metricId;
	private String dictMetricState;
	private String expressionOperator;
	private String expressionDesc;
	private String thresholdValue;
	private long timelineId;
	private String alarmTemplate;
	
	public ProfileThresholdPO(){
	}

	public long getMkId() {
		return mkId;
	}

	public void setMkId(long mkId) {
		this.mkId = mkId;
	}

	public long getProfileId() {
		return profileId;
	}

	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}

	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public String getDictMetricState() {
		return dictMetricState;
	}

	public void setDictMetricState(String dictMetricState) {
		this.dictMetricState = dictMetricState;
	}

	public String getExpressionDesc() {
		return expressionDesc;
	}

	public void setExpressionDesc(String expressionDesc) {
		this.expressionDesc = expressionDesc;
	}

	public String getExpressionOperator() {
		return expressionOperator;
	}

	public void setExpressionOperator(String expressionOperator) {
		this.expressionOperator = expressionOperator;
	}

	public long getTimelineId() {
		return timelineId;
	}

	public void setTimelineId(long timelineId) {
		this.timelineId = timelineId;
	}

	public String getThresholdValue() {
		return thresholdValue;
	}

	public void setThresholdValue(String thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	public String getAlarmTemplate() {
		return alarmTemplate;
	}

	public void setAlarmTemplate(String alarmTemplate) {
		this.alarmTemplate = alarmTemplate;
	}
}
