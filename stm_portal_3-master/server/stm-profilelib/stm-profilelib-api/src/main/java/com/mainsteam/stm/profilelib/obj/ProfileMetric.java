package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;
import java.util.List;

public class ProfileMetric implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2622956173873795658L;
	private String metric_mkId;
	private long profileId;
	private String metricId;
	private long timeLineId = -1;
	private String dictFrequencyId;
	private boolean monitor;
	private boolean alarm;
	private int alarmFlapping;
	/**
	 * 监控的指标的阈值
	 */
	private List<ProfileThreshold> metricThresholds;
	
	public ProfileMetric() {
	}

	public String getMetric_mkId() {
		return metric_mkId;
	}

	public void setMetric_mkId(String metric_mkId) {
		this.metric_mkId = metric_mkId;
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

	public String getDictFrequencyId() {
		return dictFrequencyId;
	}

	public void setDictFrequencyId(String dictFrequencyId) {
		this.dictFrequencyId = dictFrequencyId;
	}

	
	public boolean isAlarm() {
		return alarm;
	}

	public void setAlarm(boolean alarm) {
		this.alarm = alarm;
	}

	public long getTimeLineId() {
		return timeLineId;
	}

	public void setTimeLineId(long timeLineId) {
		this.timeLineId = timeLineId;
	}

	
	public void setMetricThresholds(List<ProfileThreshold> metricThresholds) {
		this.metricThresholds = metricThresholds;
	}
	
	public List<ProfileThreshold> getMetricThresholds() {
		return metricThresholds;
	}

	public boolean isMonitor() {
		return monitor;
	}

	public void setMonitor(boolean monitor) {
		this.monitor = monitor;
	}

	public int getAlarmFlapping() {
		return alarmFlapping;
	}

	public void setAlarmFlapping(int alarmFlapping) {
		this.alarmFlapping = alarmFlapping;
	}
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(10000);
		b.append("[");
		b.append(" profileId=").append(getProfileId());
		b.append(" timelineId=").append(getTimeLineId());
		b.append(" metricId=").append(getMetricId());
		b.append(" isMonitor=").append(isMonitor());
		b.append(" monitorFeq=").append(getDictFrequencyId());
		b.append(" alarmFlapping=").append(getAlarmFlapping());
		b.append("]");
		return b.toString();
	}
}
