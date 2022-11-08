/**
 * 
 */
package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;
import java.util.List;

/**
 * @author ziw
 *
 */
public class MonitorProfile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4192209996417060480L;

	private long profileId;
	
	private long parentProfileId = -1;
	
	private List<ProfileMetricMonitor> profileMetricMonitors;
	
	private List<TimelineProfileMetricMonitor> timelineProfileMetricMonitors;
	
	/**
	 * 策略与资源实例关系
	 */
	private ProfileInstanceRelation profileInstanceRelations; 
	
	/**
	 * 
	 */
	public MonitorProfile() {
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
	 * @return the timelineProfileMetricMonitors
	 */
	public final List<TimelineProfileMetricMonitor> getTimelineProfileMetricMonitors() {
		return timelineProfileMetricMonitors;
	}

	/**
	 * @param timelineProfileMetricMonitors the timelineProfileMetricMonitors to set
	 */
	public final void setTimelineProfileMetricMonitors(
			List<TimelineProfileMetricMonitor> timelineProfileMetricMonitors) {
		this.timelineProfileMetricMonitors = timelineProfileMetricMonitors;
	}

	/**
	 * @return the profileInstanceRelations
	 */
	public final ProfileInstanceRelation getProfileInstanceRelations() {
		return profileInstanceRelations;
	}

	/**
	 * @param profileInstanceRelations the profileInstanceRelations to set
	 */
	public final void setProfileInstanceRelations(
			ProfileInstanceRelation profileInstanceRelations) {
		this.profileInstanceRelations = profileInstanceRelations;
	}

	/**
	 * @return the serialversionuid
	 */
	public static final long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the parentProfileId
	 */
	public final long getParentProfileId() {
		return parentProfileId;
	}

	/**
	 * @param parentProfileId the parentProfileId to set
	 */
	public final void setParentProfileId(long parentProfileId) {
		this.parentProfileId = parentProfileId;
	}
	
}
