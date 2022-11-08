package com.mainsteam.stm.dataprocess.bigData.bo;

import java.util.Date;


/**
 * @author heshengchao
 */
public class AlarmDataLine {
	 private String instanceId;
	 private String instanceName;
	 private String instanceIp;
	 private String subInstanceId;
	 private String subInstanceName;
	 
	 private String resourceId;
	 private String metricId;
	 
	 private Date dateTime;
	 private String alarmContent;
	 private String alarmLevel;
	 
	 public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	public String getInstanceIp() {
		return instanceIp;
	}
	public void setInstanceIp(String instanceIp) {
		this.instanceIp = instanceIp;
	}
	public String getSubInstanceId() {
		return subInstanceId;
	}
	public void setSubInstanceId(String subInstanceId) {
		this.subInstanceId = subInstanceId;
	}
	public String getSubInstanceName() {
		return subInstanceName;
	}
	public void setSubInstanceName(String subInstanceName) {
		this.subInstanceName = subInstanceName;
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
	public String getAlarmContent() {
		return alarmContent;
	}
	public void setAlarmContent(String alarmContent) {
		this.alarmContent = alarmContent;
	}
	public String getAlarmLevel() {
		return alarmLevel;
	}
	public void setAlarmLevel(String alarmLevel) {
		this.alarmLevel = alarmLevel;
	}
	
}
