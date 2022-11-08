package com.mainsteam.stm.home.workbench.warning.bo;


import java.io.Serializable;
import java.util.Date;

import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.util.DateUtil;


public class Warning extends AlarmEvent implements Serializable{
	private static final long serialVersionUID = 1L;

	/**
	 * 资源名称
	 */
	private String resourceName;
	
	/**
	 * 告警内容
	 */
	private String alarmContent;
	/**
     * 资源状态(致命,严重,告警,正常,未知)
     */
    private String instanceStatus;
    /**
	 * 告警产生时间
	 */
	private String eventTime;
	/**
	 * IP地址
	 */
	private String ipAddress;
	
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getAlarmContent() {
		return alarmContent;
	}
	public void setAlarmContent(String alarmContent) {
		this.alarmContent = alarmContent;
	}
	
	public String getEventTime() {
		return eventTime;
	}
	public void setEventTime(Date datetime) {
		this.eventTime = DateUtil.format(datetime);
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
    public String getInstanceStatus() {
		return instanceStatus;
	}
	public void setInstanceStatus(String instanceStatus) {
		this.instanceStatus = instanceStatus;
	}
}
