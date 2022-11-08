package com.mainsteam.stm.scheduler;

import java.util.LinkedList;
import java.util.List;

import com.mainsteam.stm.scheduler.obj.MetricScheduleTask;
import com.mainsteam.stm.scheduler.obj.PeriodTimeFire;
import com.mainsteam.stm.scheduler.obj.ProfileScheduleTask;

/**
 * @author 作者：ziw
 * @date 创建时间：2017年2月15日 上午10:45:57
 * @version 1.0
 */
public class ProfileMetricAlignUtil {
	/**
	 * 对同一策略下的不同子策略，如果指标监控频度相同，则进行采集时间对齐。
	 * 
	 * @param deployTask
	 * @param scheduleTasks
	 * @param profileScheduleTaskMap
	 */
	public static void alignDeployProfileScheduleTask(
			ProfileScheduleTask deployTask,
			List<ProfileScheduleTask> scheduleTasks) {
		List<MetricScheduleTask> metricScheduleTasks = deployTask.getTasks();
		if (deployTask.getParentProfileId() > 0 && metricScheduleTasks != null
				&& metricScheduleTasks.size() > 0) {
			LinkedList<MetricScheduleTask> toAlignTasks = new LinkedList<>(
					metricScheduleTasks);
			/**
			 * 查找同级子策略。
			 */
			for (ProfileScheduleTask profileScheduleTask : scheduleTasks) {
				if (profileScheduleTask.getParentProfileId() == deployTask
						.getParentProfileId()) {
					List<MetricScheduleTask> otherMetricScheduleTasks = profileScheduleTask
							.getTasks();
					if (otherMetricScheduleTasks == null
							|| otherMetricScheduleTasks.size() <= 0) {
						continue;
					}
					do {
						MetricScheduleTask t = toAlignTasks.getFirst();
						if (t == null) {
							break;
						}
						PeriodTimeFire tP = t.getTimeFire();
						for (MetricScheduleTask metricScheduleTask : otherMetricScheduleTasks) {
							PeriodTimeFire mp = metricScheduleTask
									.getTimeFire();
							long lastFire = metricScheduleTask
									.getLastFireTime();
							long next = metricScheduleTask.getNextFireTime();
							if (tP.equals(mp) && (lastFire > 0)
									&& (next - lastFire <= 60000)) {// 如果监控频度小于10分钟，可以对齐。
								t.setNextFireTime(metricScheduleTask
										.getNextFireTime());
								break;
							}
						}
						toAlignTasks.removeFirst();
					} while (toAlignTasks.size()>0);
				}

				if (toAlignTasks.isEmpty()) {
					break;
				}
			}
		}
	}
}
