/**
 * 
 */
package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;

import com.mainsteam.stm.caplib.dict.FrequentEnum;

/**
 * 指标是否监控设置
 * 
 * @author ziw
 *
 */
public class ProfileMetricMonitor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9072607384100591773L;

	/**
	 * 策略id
	 */
	private long profileId;
	
	/**
	 * 基线id
	 */
	private long timelineId;
	
	/**
	 * 指标id
	 */
	private String metricId;
	
	/**
	 * 指标是否监控
	 */
	private boolean monitor;
	
	/**
	 * 指标监控频度
	 */
	private FrequentEnum monitorFeq;
	
	/**
	 * 是否是基线设置
	 */
	private boolean timeline;
	
	/**
	 * 
	 */
	public ProfileMetricMonitor() {
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
	 * @return the metricId
	 */
	public final String getMetricId() {
		return metricId;
	}

	/**
	 * @param metricId the metricId to set
	 */
	public final void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	/**
	 * @return the monitor
	 */
	public final boolean isMonitor() {
		return monitor;
	}

	/**
	 * @param monitor the monitor to set
	 */
	public final void setMonitor(boolean monitor) {
		this.monitor = monitor;
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

	/**
	 * @return the monitorFeq
	 */
	public final FrequentEnum getMonitorFeq() {
		return monitorFeq;
	}

	/**
	 * @param monitorFeq the monitorFeq to set
	 */
	public final void setMonitorFeq(FrequentEnum monitorFeq) {
		this.monitorFeq = monitorFeq;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(10000);
		b.append("[");
		b.append(" profileId=").append(getProfileId());
		b.append(" timelineId=").append(getTimelineId());
		b.append(" isTimeline=").append(isTimeline());
		b.append(" metricId=").append(getMetricId());
		b.append(" isMonitor=").append(isMonitor());
		b.append(" monitorFeq=").append(getMonitorFeq().name());
		b.append("]");
		return b.toString();
	}
}
