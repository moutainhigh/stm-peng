package com.mainsteam.stm.dataprocess.bigData.bo;

import java.util.Date;

public class MetricDataLine {
	 private long instanceId;
	 private String instanceName;
	 private long subInstanceId;
	 private String subInstanceName;
	 private String resourceId;
	 private String metricId;
	 private Date dateTime;
	 private String metricValue;
	 
	public long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public String getMetricId() {
		return metricId;
	}
	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	public long getSubInstanceId() {
		return subInstanceId;
	}
	public void setSubInstanceId(long subInstanceId) {
		this.subInstanceId = subInstanceId;
	}
	public String getSubInstanceName() {
		return subInstanceName;
	}
	public void setSubInstanceName(String subInstanceName) {
		this.subInstanceName = subInstanceName;
	}

	public String getMetricValue() {
		return metricValue;
	}

	public void setMetricValue(String metricValue) {
		this.metricValue = metricValue;
	}
}
