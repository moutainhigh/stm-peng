package com.mainsteam.stm.webService.metric;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "MetricSumaryDataBean")
public class InfoMetricSumaryDataBean{
	@XmlElement(name="instanceId",required = true)
	private long instanceId;
	@XmlElement(name="instanceName",required = true)
	private String instanceName;
	@XmlElement(name = "metricId", required = true)
	private String metricId;
	
	@XmlElement(name = "data", required = true)
	private String data;
	 
	@XmlElement(name = "unit", required = true)
	private String unit;
	@XmlElement(name = "resourceId", required = true)
    private String resourceId;
	@XmlElement(name = "collectTime", required = true)
    private Date collectTime;
	@XmlElement(name = "profileId", required = true)
    private Long profileId;
	@XmlElement(name = "timelineId",required = true)
    private Long timelineId;
	@XmlElement(name = "userFlag",required = true)
	private boolean userFlag;
	public long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}
	public String getMetricId() {
		return metricId;
	}
	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public Date getCollectTime() {
		return collectTime;
	}
	public void setCollectTime(Date collectTime) {
		this.collectTime = collectTime;
	}
	public Long getProfileId() {
		return profileId;
	}
	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}
	public Long getTimelineId() {
		return timelineId;
	}
	public void setTimelineId(Long timelineId) {
		this.timelineId = timelineId;
	}
	public boolean isUserFlag() {
		return userFlag;
	}
	public void setUserFlag(boolean userFlag) {
		this.userFlag = userFlag;
	}
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	
}
