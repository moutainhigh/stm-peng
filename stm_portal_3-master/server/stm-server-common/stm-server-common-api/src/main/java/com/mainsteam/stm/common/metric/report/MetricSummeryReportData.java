package com.mainsteam.stm.common.metric.report;

import java.util.Date;

public class MetricSummeryReportData {
	private String metricID;
	private Float max;
	private Float min;
	private Float avg;
	private String[] value;
	private long instanceID;
	private Date endTime;
	
	
	public Float getMax() {
		return max;
	}
	public void setMax(Float max) {
		this.max = max;
	}
	public Float getMin() {
		return min;
	}
	public void setMin(Float min) {
		this.min = min;
	}
	public Float getAvg() {
		return avg;
	}
	public void setAvg(Float avg) {
		this.avg = avg;
	}
	public String getMetricID() {
		return metricID;
	}
	public void setMetricID(String metricID) {
		this.metricID = metricID;
	}
	public long getInstanceID() {
		return instanceID;
	}
	public void setInstanceID(long instanceID) {
		this.instanceID = instanceID;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String[] getValue() {
		return value;
	}
	public void setValue(String[] value) {
		this.value = value;
	}
}
