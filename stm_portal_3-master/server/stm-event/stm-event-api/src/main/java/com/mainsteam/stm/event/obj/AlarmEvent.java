package com.mainsteam.stm.event.obj;

import java.util.Date;

import com.mainsteam.stm.common.metric.obj.MetricStateEnum;

public class AlarmEvent {
	private long eventID;
	private long instanceID;
	private String instanceName;
	private String metricID;
	private String eventContent;
	private MetricStateEnum eventLevel;
	private String resourceID;
	private String categoryID;
	private boolean isRecoveryEvent;
	private boolean isRecovery;
	private Date recoveryTime;
	private long recoveryEventID;
	private Date collectionTime;
	
	public String getMetricID() {
		return metricID;
	}
	public void setMetricID(String metricID) {
		this.metricID = metricID;
	}
	public long getEventID() {
		return eventID;
	}
	public void setEventID(long eventID) {
		this.eventID = eventID;
	}
	public long getInstanceID() {
		return instanceID;
	}
	public void setInstanceID(long instanceID) {
		this.instanceID = instanceID;
	}
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	public String getEventContent() {
		return eventContent;
	}
	public void setEventContent(String eventContent) {
		this.eventContent = eventContent;
	}
	public MetricStateEnum getEventLevel() {
		return eventLevel;
	}
	public void setEventLevel(MetricStateEnum level) {
		this.eventLevel = level;
	}
	public String getResourceID() {
		return resourceID;
	}
	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}
	public String getCategoryID() {
		return categoryID;
	}
	public void setCategoryID(String categoryID) {
		this.categoryID = categoryID;
	}
	public boolean isRecoveryEvent() {
		return isRecoveryEvent;
	}
	public void setRecoveryEvent(boolean isRecoveryEvent) {
		this.isRecoveryEvent = isRecoveryEvent;
	}
	public boolean isRecovery() {
		return isRecovery;
	}
	public void setRecovery(boolean isRecovery) {
		this.isRecovery = isRecovery;
	}
	public Date getRecoveryTime() {
		return recoveryTime;
	}
	public void setRecoveryTime(Date recoveryTime) {
		this.recoveryTime = recoveryTime;
	}
	public long getRecoveryEventID() {
		return recoveryEventID;
	}
	public void setRecoveryEventID(long recoveryEventID) {
		this.recoveryEventID = recoveryEventID;
	}
	public Date getCollectionTime() {
		return collectionTime;
	}
	public void setCollectionTime(Date collectionTime) {
		this.collectionTime = collectionTime;
	}
}
