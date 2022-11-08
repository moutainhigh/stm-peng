package com.mainsteam.stm.executor.obj;

import java.util.Date;


public class MetricExecuteParameter {
	/**
	 * 指标id
	 */
	private String metricId;
	
	/**
	 * 资源实例id
	 */
	private long resourceInstanceId;
	
	/**
	 * 使用的策略id
	 */
	private long profileId = -1;
	
	/**
	 * 使用的基线id
	 */
	private long timelineId = -1;
	
	/**
	 * 执行时间
	 */
	private Date executeTime;
	
	
	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public long getResourceInstanceId() {
		return resourceInstanceId;
	}

	public void setResourceInstanceId(long resourceInstanceId) {
		this.resourceInstanceId = resourceInstanceId;
	}

	/**
	 * @return the executeTime
	 */
	public final Date getExecuteTime() {
		return executeTime;
	}

	/**
	 * @param executeTime the executeTime to set
	 */
	public final void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}

	/**
	 * @return the profileId
	 */
	public final long getProfileId() {
		return profileId;
	}

	/**
	 * @param profileId the profileId to set
	 */
	public final void setProfileId(long profileId) {
		this.profileId = profileId;
	}

	/**
	 * @return the timelineId
	 */
	public final long getTimelineId() {
		return timelineId;
	}

	/**
	 * @param timelineId the timelineId to set
	 */
	public final void setTimelineId(long timelineId) {
		this.timelineId = timelineId;
	}
	
}
