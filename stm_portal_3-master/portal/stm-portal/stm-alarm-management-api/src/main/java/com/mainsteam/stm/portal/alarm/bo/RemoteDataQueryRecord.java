package com.mainsteam.stm.portal.alarm.bo;

import java.io.Serializable;
import java.util.Date;

public class RemoteDataQueryRecord implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1414152852419456165L;

	private Long recordId;
	private String remoteIp;
	private int remotePort;
	private Date queryTime;
	private Long lastAlarmId;
	
	public Long getRecordId() {
		return recordId;
	}
	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}
	public String getRemoteIp() {
		return remoteIp;
	}
	public void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}
	public int getRemotePort() {
		return remotePort;
	}
	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}
	public Date getQueryTime() {
		return queryTime;
	}
	public void setQueryTime(Date queryTime) {
		this.queryTime = queryTime;
	}
	public Long getLastAlarmId() {
		return lastAlarmId;
	}
	public void setLastAlarmId(Long lastAlarmId) {
		this.lastAlarmId = lastAlarmId;
	}
	
}
