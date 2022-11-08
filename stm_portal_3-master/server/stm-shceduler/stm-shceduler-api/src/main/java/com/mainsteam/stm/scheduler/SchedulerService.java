/**
 * 
 */
package com.mainsteam.stm.scheduler;

import java.util.List;

import com.mainsteam.stm.scheduler.obj.CustomMetricScheduleTask;
import com.mainsteam.stm.scheduler.obj.LimitedPeriodTimeFire;
import com.mainsteam.stm.scheduler.obj.MetricScheduleTask;
import com.mainsteam.stm.scheduler.obj.ProfileScheduleTask;
import com.mainsteam.stm.scheduler.obj.TimelineScheduleTask;

/**
 * 指标调度
 * 
 * @author ziw
 * 
 */
public interface SchedulerService {
	/**
	 * 开始策略调度。如果策略调度任务已经存在，则覆盖原有的调度任务
	 * 
	 * @param schceduleTask
	 */
	public void schedule(ProfileScheduleTask scheduleTask);

	/**
	 * 使用指定的策略添加资源实例监控
	 * 
	 * @param profileId
	 *            策略id
	 * @param resourceInstIds
	 *            资源实例id列表
	 */
	public void schedule(long profileId, long[] resourceInstIds);

	/**
	 * 开始调度一个指标。如果指标调度任务已经存在，则覆盖原有的调度任务
	 * 
	 * @param scheduleTask
	 */
	public void schedule(MetricScheduleTask scheduleTask);

	/**
	 * 开始调度一个自定义指标。如果指标调度任务已经存在，则覆盖原有的调度任务
	 * 
	 * @param scheduleTask
	 */
	public void schedule(CustomMetricScheduleTask scheduleTask);

	/**
	 * 添加一个基线设置
	 * 
	 * @param scheduleTask
	 *            基线设置
	 */
	public void schedule(TimelineScheduleTask scheduleTask);

	/**
	 * 更新时间基线的时间段
	 * 
	 * @param timelineId
	 * @param limitedPeriodTimeFire
	 */
	public void updateTimeline(long timelineId,
			LimitedPeriodTimeFire limitedPeriodTimeFire);

	/**
	 * 删除资源实例的调度
	 * 
	 * @param resourceInstId
	 *            资源实例id
	 */
	public void removeScheduleByResourceInstanceIds(long[] resourceInstId);

	/**
	 * 删除资源实例的调度
	 * 
	 * @param resourceInstId
	 *            资源实例id
	 */
	public void removeCustomMetricTasksByResourceInstanceIds(
			long[] resourceInstId);

	/**
	 * 取消策略的指标的调度
	 * 
	 * @param profileId
	 *            策略id
	 * @param metricId
	 *            指标id
	 */
	public void cancelScheduleByProfileId(long profileId, String metricId);

	/**
	 * 取消策略的指标的调度
	 * 
	 * @param profileId
	 *            策略id
	 * @param metricId
	 *            指标id
	 */
	public void removeMetricScheduleTaskByProfileId(long profileId,
			String metricId);

	/**
	 * 删除自定义指标设置
	 * 
	 * @param instanceId
	 * 
	 * @param metricId
	 */
	public void removeCustomMetricScheduleTaskByInstanceId(long profileId,
			long instanceId, String metricId);

	/**
	 * 取消基线的指标的调度
	 * 
	 * @param profileIds
	 *            策略id列表
	 * @param metricId
	 *            指标id
	 */
	public void cancelScheduleByTimelineId(long timelineId, String metricId);

	/**
	 * 删除一个基线设置
	 * 
	 * @param timelineId
	 */
	public void removeScheduleByTimeline(long timelineId);

	/**
	 * 删除策略的指标的调度
	 * 
	 * @param profileIds
	 *            策略id列表
	 */
	public void removeSchedule(List<Long> profileIds);

	/**
	 * 查询某一个指标的调度
	 * 
	 * @param profileId
	 *            策略id
	 * @param metricId
	 *            指标id
	 */
	public MetricScheduleTask getScheduleTask(long profileId, String metricId);

	/**
	 * 查询某一个指标的调度
	 * 
	 * @param profileId
	 *            策略id
	 * @param metricId
	 *            指标id
	 */
	public CustomMetricScheduleTask getCustomMetricScheduleTask(long profileId,
			long instanceId, String metricId);

	/**
	 * 查询某一个指标的调度
	 * 
	 * @param metricId
	 *            自定义指标id
	 */
	public List<CustomMetricScheduleTask> getCustomMetricScheduleTasksByMetricId(
			String metricId);

	/**
	 * 查询策略的调度任务
	 * 
	 * @param profileId
	 * 
	 * @return ProfileSchceduleTask
	 */
	public ProfileScheduleTask getProfileScheduleTask(long profileId);

	/**
	 * 列出所有的策略调度任务
	 * 
	 * @return List<String> profileId列表
	 */
	public List<Long> listProfileScheduleTasks();

	/**
	 * 判断是否有策略的设置
	 * 
	 * @param profileId
	 * @return
	 */
	public boolean hasProfileScheduleTask(long profileId);
}
