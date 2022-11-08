/**
 * 
 */
package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.mainsteam.stm.profilelib.objenum.TimelineTypeEnum;

/**
 * @author ziw
 *
 */
public class TimelineProfileMetricMonitor implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3540092085660227550L;
	
	private long profileId;

	private long timelineId;
	
	private List<ProfileMetricMonitor> profileMetricMonitors;
	
	private Date startTime;

    private Date endTime;
    
    private TimelineTypeEnum timelineType;
    
	/**
	 * 
	 */
	public TimelineProfileMetricMonitor() {
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
	 * @return the profileMetricMonitors
	 */
	public final List<ProfileMetricMonitor> getProfileMetricMonitors() {
		return profileMetricMonitors;
	}


	/**
	 * @param profileMetricMonitors the profileMetricMonitors to set
	 */
	public final void setProfileMetricMonitors(
			List<ProfileMetricMonitor> profileMetricMonitors) {
		this.profileMetricMonitors = profileMetricMonitors;
	}


	/**
	 * @return the startTime
	 */
	public final Date getStartTime() {
		return startTime;
	}


	/**
	 * @param startTime the startTime to set
	 */
	public final void setStartTime(Date startTime) {
		this.startTime = startTime;
	}


	/**
	 * @return the endTime
	 */
	public final Date getEndTime() {
		return endTime;
	}


	/**
	 * @param endTime the endTime to set
	 */
	public final void setEndTime(Date endTime) {
		this.endTime = endTime;
	}


	/**
	 * @return the timelineType
	 */
	public final TimelineTypeEnum getTimelineType() {
		return timelineType;
	}


	/**
	 * @param timelineType the timelineType to set
	 */
	public final void setTimelineType(TimelineTypeEnum timelineType) {
		this.timelineType = timelineType;
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
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(10000);
		b.append("[");
		b.append(" profileId=").append(getProfileId());
		b.append(" timelineId=").append(getTimelineId());
		b.append(" timelineType=").append(getTimelineType());
		b.append(" startTime=").append(getStartTime());
		b.append(" endTime=").append(getEndTime());
		b.append(" metric list:");
		if(profileMetricMonitors != null){
			for (ProfileMetricMonitor profileMetricMonitor : profileMetricMonitors) {
				b.append(" \n").append(profileMetricMonitor);
			}
		}
		b.append("]");
		return b.toString();
	}
}
