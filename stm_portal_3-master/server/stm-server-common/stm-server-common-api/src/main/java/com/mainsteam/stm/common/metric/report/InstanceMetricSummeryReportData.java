package com.mainsteam.stm.common.metric.report;

import java.util.Date;
import java.util.List;

public class InstanceMetricSummeryReportData {
	
	private List<MetricSummeryReportData> metricData;
	private Date startTime;
	private Date endTime;
	private long instanceID;
	
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
	public long getInstanceID() {
		return instanceID;
	}
	public void setInstanceID(long instanceID) {
		this.instanceID = instanceID;
	}
	
	public List<MetricSummeryReportData> getMetricData() {
		return metricData;
	}

	public void setMetricData(List<MetricSummeryReportData> metricData) {
		this.metricData = metricData;
	}
}
