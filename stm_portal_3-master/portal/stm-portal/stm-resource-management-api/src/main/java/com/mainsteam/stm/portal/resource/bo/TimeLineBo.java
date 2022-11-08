package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;
import java.util.Date;

import com.mainsteam.stm.profilelib.objenum.TimelineTypeEnum;

public class TimeLineBo implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	private long profileId;
	private long timeLineId;
	private String name;
	private Date startTime;
	private Date endTime;
	private TimelineTypeEnum timeLineType;
	private MetricSettingBo metricSetting;
	
	
	
	public long getProfileId() {
		return profileId;
	}
	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
	public long getTimeLineId() {
		return timeLineId;
	}
	public void setTimeLineId(long timeLineId) {
		this.timeLineId = timeLineId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
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
	public TimelineTypeEnum getTimeLineType() {
		return timeLineType;
	}
	public void setTimeLineType(TimelineTypeEnum timeLineType) {
		this.timeLineType = timeLineType;
	}
	public MetricSettingBo getMetricSetting() {
		return metricSetting;
	}
	public void setMetricSetting(MetricSettingBo metricSetting) {
		this.metricSetting = metricSetting;
	}
	
}
