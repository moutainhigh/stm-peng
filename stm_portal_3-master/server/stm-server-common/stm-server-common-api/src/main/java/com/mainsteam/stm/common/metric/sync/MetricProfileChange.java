package com.mainsteam.stm.common.metric.sync;

public class MetricProfileChange {

	private long profileID;
	private String metricID;
	private Boolean isMonitor;
	private Boolean isAlarm;
	private long instanceId;
	private Boolean isAlarmConfirm;//是否为告警确认
	private Boolean isCustomMetric; //是否为自定义指标

	public long getProfileID() {
		return profileID;
	}
	public Boolean getIsMonitor() {
		return isMonitor;
	}
	public void setIsMonitor(Boolean isMonitor) {
		this.isMonitor = isMonitor;
	}
	public Boolean getIsAlarm() {
		return isAlarm;
	}
	public void setIsAlarm(Boolean isAlarm) {
		this.isAlarm = isAlarm;
	}
	public void setProfileID(long profileID) {
		this.profileID = profileID;
	}
	public String getMetricID() {
		return metricID;
	}
	public void setMetricID(String metricID) {
		this.metricID = metricID;
	}

	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public Boolean getAlarmConfirm() {
		return isAlarmConfirm;
	}

	public void setAlarmConfirm(Boolean alarmConfirm) {
		isAlarmConfirm = alarmConfirm;
	}

	public Boolean getCustomMetric() {
		return isCustomMetric;
	}

	public void setCustomMetric(Boolean customMetric) {
		isCustomMetric = customMetric;
	}

	@Override
	public String toString() {
		return "MetricProfileChange{" +
				"profileID=" + profileID +
				", metricID='" + metricID + '\'' +
				", isMonitor=" + isMonitor +
				", isAlarm=" + isAlarm +
				", instanceId=" + instanceId +
				", isAlarmConfirm=" + isAlarmConfirm +
				'}';
	}
}
