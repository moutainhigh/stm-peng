/**
 * 
 */
package com.mainsteam.stm.scheduler.obj;

import java.util.List;

/**
 * @author ziw
 * 
 */
public class ProfileScheduleTask extends ScheduleTask {

	private List<TimelineScheduleTask> schceduleTasks;

	private long[] resourceInstIds;

	private int addResourceInstLength = 0;

	/**
	 * 
	 */
	public ProfileScheduleTask() {
	}

	/**
	 * @return the schceduleTasks
	 */
	public final List<TimelineScheduleTask> getTimelineScheduleTasks() {
		return schceduleTasks;
	}

	/**
	 * @param schceduleTasks
	 *            the schceduleTasks to set
	 */
	public final void setTimelineScheduleTasks(
			List<TimelineScheduleTask> schceduleTasks) {
		this.schceduleTasks = schceduleTasks;
	}

	/**
	 * @return the resourceInstIds
	 */
	public final long[] getResourceInstIds() {
		return resourceInstIds;
	}

	/**
	 * @param resourceInstIds
	 *            the resourceInstIds to set
	 */
	public final void setResourceInstIds(long[] resourceInstIds) {
		this.resourceInstIds = resourceInstIds;
	}

	public void clearAddedResourceInstLength() {
		this.addResourceInstLength = 0;
	}

	public int getAddedResourceInstLength() {
		return this.addResourceInstLength;
	}
}
