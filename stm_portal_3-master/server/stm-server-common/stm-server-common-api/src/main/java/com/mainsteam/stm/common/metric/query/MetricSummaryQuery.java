package com.mainsteam.stm.common.metric.query;

import java.util.Date;

import com.mainsteam.stm.common.metric.obj.MetricSummaryType;

public class MetricSummaryQuery {

	private String metricID;
	private long instanceID;
	private String summaryType;
	private Date startTime;
	private Date endTime;
	
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getSummaryType() {
		return summaryType;
	}
	public void setSummaryType(MetricSummaryType metricSummaryType) {
		this.summaryType = metricSummaryType.name();
	}
	public String getMetricID() {
		return metricID;
	}
	public void setMetricID(String metriID) {
		this.metricID = metriID;
	}
	public long getInstanceID() {
		return instanceID;
	}
	public void setInstanceID(long instanceID) {
		this.instanceID = instanceID;
	}
}
