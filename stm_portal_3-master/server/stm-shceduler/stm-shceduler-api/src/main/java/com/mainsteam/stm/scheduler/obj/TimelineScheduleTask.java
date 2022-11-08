/**
 * 
 */
package com.mainsteam.stm.scheduler.obj;


/**
 * @author ziw
 * 
 */
public class TimelineScheduleTask extends ScheduleTask {

	/**
	 * 受限制的时间段调度
	 */
	private LimitedPeriodTimeFire limitedPeriodTimeFire;

	/**
	 * 基线id
	 */
	private long timelineId;

	/**
	 * 
	 */
	public TimelineScheduleTask() {
	}

	/**
	 * @return the limitedPeriodTimeFire
	 */
	public final LimitedPeriodTimeFire getLimitedPeriodTimeFire() {
		return limitedPeriodTimeFire;
	}



	/**
	 * @param limitedPeriodTimeFire the limitedPeriodTimeFire to set
	 */
	public final void setLimitedPeriodTimeFire(
			LimitedPeriodTimeFire limitedPeriodTimeFire) {
		this.limitedPeriodTimeFire = limitedPeriodTimeFire;
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
