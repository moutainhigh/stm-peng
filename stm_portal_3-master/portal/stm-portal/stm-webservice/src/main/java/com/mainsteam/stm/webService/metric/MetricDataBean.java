package com.mainsteam.stm.webService.metric;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "MetricDataBean")
public class MetricDataBean {
	@XmlElement(name="resourceInstanceId",required = true)
	private long resourceInstanceId;
	@XmlElement(name="data",required = true)
	private String data;
	@XmlElement(name="metricId",required = true)
	private String metricId;
	@XmlElement(name="collectTime",required = true)
	private Date collectTime;
	@XmlElement(name="profileId",required = true)
	private Long profileId;
	@XmlElement(name="timelineId",required = true)
	private Long timelineId;

	public long getResourceInstanceId() {
		return resourceInstanceId;
	}

	public void setResourceInstanceId(long resourceInstanceId) {
		this.resourceInstanceId = resourceInstanceId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
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

	 @Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{resourceInstanceId:"+resourceInstanceId)
		.append(",data:"+data)
		.append(",metricId:"+metricId)
		.append(",collectTime:"+collectTime)
		.append(",profileId:"+profileId)
		.append(",timelineId:"+timelineId);
		return sb.toString();
	}
}
