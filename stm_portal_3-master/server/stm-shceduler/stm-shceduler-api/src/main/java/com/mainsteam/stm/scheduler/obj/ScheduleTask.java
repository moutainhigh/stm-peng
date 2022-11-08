/**
 * 
 */
package com.mainsteam.stm.scheduler.obj;

import java.util.List;

/**
 * @author ziw
 *
 */
public class ScheduleTask  {
	
	/**
	 * 调度task
	 */
	private List<MetricScheduleTask> tasks;
	
	private long profileId;
	
	private long parentProfileId = -1;

	/**
	 * @return the tasks
	 */
	public final List<MetricScheduleTask> getTasks() {
		return tasks;
	}

	/**
	 * @param tasks the tasks to set
	 */
	public final void setTasks(List<MetricScheduleTask> tasks) {
		this.tasks = tasks;
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

	/**
	 * 
	 */
	public ScheduleTask() {
	}

}
