/**
 * 
 */
package com.mainsteam.stm.scheduler.obj;


/**
 * @author ziw
 * 
 */
public class MetricScheduleTask {
	

	/**
	 * 策略id
	 */
	private long profileId;

	/**
	 * 时间基线id
	 */
	private long timelineId;
	
	/**
	 * 是否是基线设置
	 */
	private boolean timeline;
	
	/**
	 * 指标id
	 */
	private String metricId;
	
	/**
	 * 是否活动
	 */
	private boolean isActive;

	/**
	 * 默认时间调度
	 */
	private PeriodTimeFire timeFire;

	/**
	 * 上一次的调度时间
	 */
	private long lastFireTime = -1;
	
	private long nextFireTime = -1;

	/**
	 * 
	 */
	public MetricScheduleTask() {
	}

	/**
	 * @return the metricId
	 */
	public final String getMetricId() {
		return metricId;
	}

	/**
	 * @param metricId
	 *            the metricId to set
	 */
	public final void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	/**
	 * @return the timeFire
	 */
	public final PeriodTimeFire getTimeFire() {
		return timeFire;
	}

	/**
	 * @param timeFire
	 *            the timeFire to set
	 */
	public final void setTimeFire(PeriodTimeFire timeFire) {
		this.timeFire = timeFire;
	}

	/**
	 * @return the profileId
	 */
	public final long getProfileId() {
		return profileId;
	}

	/**
	 * @param profileId
	 *            the profileId to set
	 */
	public final void setProfileId(long profileId) {
		this.profileId = profileId;
	}

	/**
	 * @return the lastFireTime
	 */
	public final long getLastFireTime() {
		return lastFireTime;
	}

	/**
	 * @param lastFireTime
	 *            the lastFireTime to set
	 */
	public final void setLastFireTime(long lastFireTime) {
		this.lastFireTime = lastFireTime;
	}
	

	/**
	 * @return the isActive
	 */
	public final boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public final void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	

	/**
	 * @return the nextFireTime
	 */
	public final long getNextFireTime() {
		return nextFireTime;
	}

	/**
	 * @param nextFireTime the nextFireTime to set
	 */
	public final void setNextFireTime(long nextFireTime) {
		this.nextFireTime = nextFireTime;
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

	/**
	 * @return the timeline
	 */
	public final boolean isTimeline() {
		return timeline;
	}

	/**
	 * @param timeline the timeline to set
	 */
	public final void setTimeline(boolean timeline) {
		this.timeline = timeline;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("profileId=").append(profileId);
		b.append(" metricId=").append(metricId);
		return b.toString();
	}
}
