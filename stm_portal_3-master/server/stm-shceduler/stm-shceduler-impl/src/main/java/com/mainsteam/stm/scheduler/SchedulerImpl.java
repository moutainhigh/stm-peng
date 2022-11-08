/**
 * 
 */
package com.mainsteam.stm.scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.mainsteam.stm.executor.MetricExecutor;
import com.mainsteam.stm.executor.obj.CustomMetricExecuteParameter;
import com.mainsteam.stm.executor.obj.CustomMetricPluginParameter;
import com.mainsteam.stm.executor.obj.MetricExecuteParameter;
import com.mainsteam.stm.scheduler.obj.CustomMetricScheduleTask;
import com.mainsteam.stm.scheduler.obj.LimitedPeriodTimeFire;
import com.mainsteam.stm.scheduler.obj.MetricScheduleTask;
import com.mainsteam.stm.scheduler.obj.PeriodTimeFire;
import com.mainsteam.stm.scheduler.obj.ProfileScheduleTask;
import com.mainsteam.stm.scheduler.obj.ScheduleTask;
import com.mainsteam.stm.scheduler.obj.TimelineScheduleTask;

/**
 * 调度实现
 * 
 * @author ziw
 * 
 */
public class SchedulerImpl implements SchedulerService,
		ApplicationListener<ContextRefreshedEvent> {

	private static final Log logger = LogFactory.getLog(SchedulerService.class);

	/**
	 * 保证只有一个实例再运行
	 */
	private static boolean running = false;

	private long offset_threshold = 10 * 60 * 1000;// ms

	private Map<Long, ProfileScheduleTask> profileScheduleTaskMap;

	private Map<Long, TimelineScheduleTask> timelineScheduleTaskMap;

	private List<ProfileScheduleTask> scheduleTasks;

	private MetricExecutor metricExecutor;

	private TaskDriver taskDriver;

	/**
	 * 最小扫描时间间隔
	 */
	private long minFireTimeLength = 20;// ms

	/**
	 * 计算后的最小间隔时间，该事件不能比minFireTimeLength大
	 */
	private long minFireDate = -1;

	/**
	 * 
	 */
	public SchedulerImpl() {
		scheduleTasks = new ArrayList<>(100);
		profileScheduleTaskMap = new HashMap<>(500);
		timelineScheduleTaskMap = new HashMap<>(500 * 6);
	}

	/**
	 * @return the offset_threshold
	 */
	protected final long getOffset_threshold() {
		return offset_threshold;
	}

	/**
	 * @param offset_threshold
	 *            the offset_threshold to set
	 */
	protected final void setOffset_threshold(long offset_threshold) {
		this.offset_threshold = offset_threshold;
	}

	public synchronized void stop() {
		running = false;
	}

	public synchronized void start() {
		if (logger.isTraceEnabled()) {
			logger.trace("start Scheduler.");
		}
		synchronized (SchedulerService.class) {
			if (running) {
				return;
			}
			taskDriver = new TaskDriver();
			running = true;
			Thread t = new Thread(taskDriver, "MetricSchedulerTaskDriver");
			t.start();
		}
		if (logger.isTraceEnabled()) {
			logger.trace("start end");
		}
	}

	/**
	 * @param metricExecutor
	 *            the metricExecutor to set
	 */
	public synchronized final void setMetricExecutor(
			MetricExecutor metricExecutor) {
		this.metricExecutor = metricExecutor;
	}

	@Override
	public synchronized void schedule(ProfileScheduleTask scheduleTask) {
		if (logger.isTraceEnabled()) {
			logger.trace("schedule start profileId="
					+ scheduleTask.getProfileId());
		}
		Long profileId = scheduleTask.getProfileId();
		/**
		 * 移除上一次的设置
		 */
		if (profileScheduleTaskMap.containsKey(profileId)) {
			ProfileScheduleTask oldTask = profileScheduleTaskMap.get(profileId);
			scheduleTasks.remove(oldTask);
			List<TimelineScheduleTask> baseLineProfileScheduleTasks = oldTask
					.getTimelineScheduleTasks();
			if (baseLineProfileScheduleTasks != null
					&& baseLineProfileScheduleTasks.size() > 0) {
				for (TimelineScheduleTask timelineTask : baseLineProfileScheduleTasks) {
					timelineScheduleTaskMap
							.remove(timelineTask.getTimelineId());
				}
			}
		}
		if (scheduleTask.getResourceInstIds() != null
				&& scheduleTask.getResourceInstIds().length > 0) {
			Arrays.sort(scheduleTask.getResourceInstIds());
		}

		ProfileMetricAlignUtil.alignDeployProfileScheduleTask(scheduleTask,
				scheduleTasks);

		profileScheduleTaskMap.put(scheduleTask.getProfileId(), scheduleTask);

		/**
		 * 保证父策略在前面
		 */
		if (scheduleTask.getParentProfileId() <= 0) {
			scheduleTasks.add(0, scheduleTask);
		} else {
			scheduleTasks.add(scheduleTask);
		}
		List<TimelineScheduleTask> baseLineProfileScheduleTasks = scheduleTask
				.getTimelineScheduleTasks();
		if (baseLineProfileScheduleTasks != null
				&& baseLineProfileScheduleTasks.size() > 0) {
			for (TimelineScheduleTask timelineTask : baseLineProfileScheduleTasks) {
				timelineScheduleTaskMap.put(timelineTask.getTimelineId(),
						timelineTask);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("schedule end");
		}
	}

	@Override
	public synchronized void schedule(MetricScheduleTask scheduleTask) {
		Long profileId = scheduleTask.getProfileId();
		String metricId = scheduleTask.getMetricId();
		if (logger.isTraceEnabled()) {
			logger.trace("schedule start " + scheduleTask.toString());
		}
		if (profileScheduleTaskMap.containsKey(profileId)) {
			List<MetricScheduleTask> tasks = null;
			if (scheduleTask.isTimeline()) {
				/**
				 * 从基线设置中找
				 */
				if (timelineScheduleTaskMap.containsKey(scheduleTask
						.getTimelineId())) {
					TimelineScheduleTask timelineScheduleTask = timelineScheduleTaskMap
							.get(scheduleTask.getTimelineId());
					tasks = timelineScheduleTask.getTasks();
					if (tasks == null) {
						tasks = new ArrayList<>();
						tasks.add(scheduleTask);
						timelineScheduleTask.setTasks(tasks);
					}
				} else {
					if (logger.isErrorEnabled()) {
						logger.error("schedule cannt found timeline "
								+ scheduleTask.getTimelineId()
								+ " from profile " + profileId);
					}
				}
			} else {
				ScheduleTask profileScheduleTask = profileScheduleTaskMap
						.get(profileId);
				tasks = profileScheduleTask.getTasks();
				if (tasks == null) {
					tasks = new ArrayList<>();
					tasks.add(scheduleTask);
					profileScheduleTask.setTasks(tasks);
				}
			}
			if (tasks != null) {
				MetricScheduleTask findTask = null;
				for (MetricScheduleTask metricScheduleTask : tasks) {
					if (metricId.equals(metricScheduleTask.getMetricId())) {
						if (metricScheduleTask instanceof CustomMetricScheduleTask) {
							if (((CustomMetricScheduleTask) metricScheduleTask)
									.getPluginParameter().getInstanceId() == ((CustomMetricScheduleTask) scheduleTask)
									.getPluginParameter().getInstanceId()) {
								findTask = metricScheduleTask;
								break;
							}
						} else {
							findTask = metricScheduleTask;
							break;
						}
					}
				}
				if (findTask != null) {
					findTask.setActive(scheduleTask.isActive());
					if (scheduleTask.isActive()) {
						/**
						 * 如果监控频度改变，则判断新的监控频度，下一次执行时间是否比旧的监控频度触发的早，如果是，下一次执行
						 * 
						 * 采用新的监控频度。
						 */
						long lastFireTime = findTask.getLastFireTime();
						if (lastFireTime > 0) {
							PeriodTimeFire p = scheduleTask.getTimeFire();
							long newTaskNextFireTime = p
									.getNextTimeFire(lastFireTime);
							if (findTask.getNextFireTime() > newTaskNextFireTime) {
								findTask.setNextFireTime(newTaskNextFireTime);
							}
						}
					}
					findTask.setTimeFire(scheduleTask.getTimeFire());
				} else {
					tasks.add(scheduleTask);
				}
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("schedule. no profile task was found."
						+ scheduleTask.toString());
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("schedule end");
		}
	}

	@Override
	public synchronized MetricScheduleTask getScheduleTask(long profileId,
			String metricId) {
		if (logger.isTraceEnabled()) {
			logger.trace("getScheduleTask start");
		}
		MetricScheduleTask findTask = null;
		ScheduleTask scheduleTask = profileScheduleTaskMap.get(profileId);
		if (scheduleTask != null) {
			List<MetricScheduleTask> tasks = scheduleTask.getTasks();
			if (tasks != null && tasks.size() > 0) {
				for (MetricScheduleTask metricScheduleTask : tasks) {
					if (metricId.equals(metricScheduleTask.getMetricId())) {
						findTask = metricScheduleTask;
						break;
					}
				}
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("getScheduleTask profile task not found. profileId="
						+ profileId);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getScheduleTask end");
		}
		return findTask;
	}

	@Override
	public synchronized ProfileScheduleTask getProfileScheduleTask(
			long profileId) {
		return profileScheduleTaskMap.get(profileId);
	}

	@Override
	public synchronized List<Long> listProfileScheduleTasks() {
		return new ArrayList<>(profileScheduleTaskMap.keySet());
	}

	private synchronized void decreaseFiredTasksNextFireTime(long offset) {
		for (ProfileScheduleTask task : this.scheduleTasks) {
			List<MetricScheduleTask> mTasks = task.getTasks();
			if (mTasks != null) {
				for (MetricScheduleTask mtask : mTasks) {
					if (mtask.isActive() && mtask.getNextFireTime() > 0) {
						mtask.setNextFireTime(mtask.getNextFireTime() - offset);
					}
				}
			}
			List<TimelineScheduleTask> timelineScheduleTasks = task
					.getTimelineScheduleTasks();
			if (timelineScheduleTasks != null) {
				for (TimelineScheduleTask timelineScheduleTask : timelineScheduleTasks) {
					mTasks = timelineScheduleTask.getTasks();
					if (mTasks != null) {
						for (MetricScheduleTask mtask : mTasks) {
							if (mtask.isActive() && mtask.getNextFireTime() > 0) {
								mtask.setNextFireTime(mtask.getNextFireTime()
										- offset);
							}
						}
					}
				}
			}
		}
	}

	private class TaskDriver implements Runnable {
		public void run() {
			while (running) {
				Date currentDate = new Date();
				long currentDateValue = currentDate.getTime();
				List<FireTaskWrapper> firedTasks = getFiredTaks(currentDateValue);
				if (firedTasks.size() > 0) {
					if (logger.isInfoEnabled()) {
						logger.info("firedTasks size=" + firedTasks.size());
					}
					fireTask(firedTasks, currentDate);
				} else if (minFireDate == -1) {
					minFireDate = currentDateValue;
				}
				long afterCurrentDateValue = System.currentTimeMillis();
				if (afterCurrentDateValue < currentDateValue) {
					/**
					 * 
					 * 计算机时间被修改了
					 * 
					 * 计算机时间向之前修改了
					 * 
					 * 将所有被调度过的任务的下次执行时间进行差值计算
					 */
					long offset = currentDateValue - afterCurrentDateValue;
					if (offset >= 60000) {
						decreaseFiredTasksNextFireTime(offset);
					}
				}
				long waitFireTimeLength = minFireDate
						- System.currentTimeMillis();
				if (waitFireTimeLength < minFireTimeLength) {
					waitFireTimeLength = minFireTimeLength;
				}
				minFireDate = -1;
				synchronized (this) {
					try {
						this.wait(waitFireTimeLength);
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}

	private List<MetricScheduleTask> getValidMetricScheduleTasks(
			ProfileScheduleTask dft, long currentDate) {
		List<MetricScheduleTask> profileMetricTasks = null;

		List<TimelineScheduleTask> timeLineTasks = dft
				.getTimelineScheduleTasks();
		TimelineScheduleTask limitTask = null;
		if (timeLineTasks != null && timeLineTasks.size() > 0) {
			for (TimelineScheduleTask baseLineProfileScheduleTask : timeLineTasks) {
				if (baseLineProfileScheduleTask.getLimitedPeriodTimeFire()
						.isValid(currentDate)) {
					limitTask = baseLineProfileScheduleTask;
					break;
				}
			}
		}

		if (limitTask != null) {
			profileMetricTasks = limitTask.getTasks();
		} else {
			profileMetricTasks = dft.getTasks();
		}
		return profileMetricTasks;
	}

	private List<FireTaskWrapper> getFiredTasks(ProfileScheduleTask dft,
			long currentDate) {
		List<FireTaskWrapper> toFiredTasks = new ArrayList<>(100);
		List<MetricScheduleTask> profileMetricTasks = getValidMetricScheduleTasks(
				dft, currentDate);
		if (profileMetricTasks != null && profileMetricTasks.size() > 0) {
			List<MetricScheduleTask> firedTasks = new ArrayList<>();
			List<CustomMetricScheduleTask> customMetricFireTasks = new ArrayList<>();
			for (MetricScheduleTask metricScheduleTask : profileMetricTasks) {
				if (metricScheduleTask.isActive()) {
					if (metricScheduleTask.getLastFireTime() == -1
							|| metricScheduleTask.getNextFireTime() <= currentDate) {
						metricScheduleTask.setNextFireTime(metricScheduleTask
								.getTimeFire().getNextTimeFire(currentDate));
						metricScheduleTask.setLastFireTime(currentDate);
						if (metricScheduleTask instanceof CustomMetricScheduleTask) {
							customMetricFireTasks
									.add((CustomMetricScheduleTask) metricScheduleTask);
						} else {
							firedTasks.add(metricScheduleTask);
						}
					}
					if (minFireDate == -1) {
						minFireDate = metricScheduleTask.getNextFireTime();
					} else if (minFireDate > metricScheduleTask
							.getNextFireTime()) {
						minFireDate = metricScheduleTask.getNextFireTime();
					}
				}
			}
			if (firedTasks.size() > 0 || customMetricFireTasks.size() > 0) {
				FireTaskWrapper fireTaskWrapper = new FireTaskWrapper();
				fireTaskWrapper.firedTasks = firedTasks;
				fireTaskWrapper.customMetricFireTasks = customMetricFireTasks;
				fireTaskWrapper.resourceInstIds = dft.getResourceInstIds();
				if (logger.isTraceEnabled()) {
					StringBuilder b = new StringBuilder();
					b.append("getFiredTasks profileId=").append(
							dft.getProfileId());
					b.append(" firedTasks.size=").append(
							fireTaskWrapper.firedTasks.size());
					b.append(" customMetricFireTasks.size=").append(
							fireTaskWrapper.customMetricFireTasks.size());
					b.append(" resourceInstIds.size=").append(
							fireTaskWrapper.resourceInstIds.length);
					logger.trace(b.toString());
				}
				toFiredTasks.add(fireTaskWrapper);
			}
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("getFiredTasks  empty metric task was found in profile."
						+ dft.getProfileId());
			}
		}
		if (toFiredTasks.size() <= 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("getFiredTasks no metric task was fired in profile."
						+ dft.getProfileId());
			}
		}
		return toFiredTasks;
	}

	private synchronized List<FireTaskWrapper> getFiredTaks(long currentDate) {
		List<FireTaskWrapper> toFiredTasks = new ArrayList<>(50000);
		for (ProfileScheduleTask dft : scheduleTasks) {
			/**
			 * 如果策略中没有资源实例，则直接取消本次调度
			 */
			if (dft.getResourceInstIds() == null
					|| dft.getResourceInstIds().length == 0) {
				continue;
			}
			toFiredTasks.addAll(getFiredTasks(dft, currentDate));
		}
		return toFiredTasks;
	}

	private void fireTask(List<FireTaskWrapper> firedTasks, Date currentDate) {
		List<MetricExecuteParameter> executeParameters = new ArrayList<>(
				firedTasks.size());
		for (FireTaskWrapper wrapper : firedTasks) {
			if (wrapper.firedTasks.size() > 0) {
				for (long resourceInstId : wrapper.resourceInstIds) {
					List<MetricScheduleTask> metricScheduleTasks = wrapper.firedTasks;
					for (MetricScheduleTask task : metricScheduleTasks) {
						String metricId = task.getMetricId();
						MetricExecuteParameter p = new MetricExecuteParameter();
						p.setExecuteTime(currentDate);
						p.setMetricId(metricId);
						p.setResourceInstanceId(resourceInstId);
						p.setProfileId(task.getProfileId());
						p.setTimelineId(task.getTimelineId());
						executeParameters.add(p);
					}
				}
			}
			if (wrapper.customMetricFireTasks.size() > 0) {
				for (CustomMetricScheduleTask task : wrapper.customMetricFireTasks) {
					CustomMetricExecuteParameter p = new CustomMetricExecuteParameter();
					p.setExecuteTime(currentDate);
					p.setMetricId(task.getPluginParameter().getCustomMetricId());
					p.setResourceInstanceId(task.getPluginParameter()
							.getInstanceId());
					p.setProfileId(task.getProfileId());
					// p.setTimelineId(task.getTimelineId());
					CustomMetricPluginParameter pluginParameter = task
							.getPluginParameter() == null ? null : task
							.getPluginParameter().clone();
					p.setPluginParameter(pluginParameter);
					executeParameters.add(p);
				}
			}
		}
		try {
			metricExecutor.execute(executeParameters);
		} catch (Throwable e) {
			if (logger.isErrorEnabled()) {
				logger.error("fireTask", e);
			}
		}
	}

	private class FireTaskWrapper {
		List<MetricScheduleTask> firedTasks = null;
		List<CustomMetricScheduleTask> customMetricFireTasks = null;
		long[] resourceInstIds;
	}

	@Override
	public synchronized void schedule(long profileId, long[] resourceInstIds) {
		if (logger.isTraceEnabled()) {
			logger.trace("schedule resourceInstIds start profileId="
					+ profileId);
		}
		if (profileScheduleTaskMap.containsKey(profileId)) {
			ScheduleTask task = profileScheduleTaskMap.get(profileId);
			if (task instanceof ProfileScheduleTask) {
				ProfileScheduleTask profileScheduleTask = (ProfileScheduleTask) task;
				long[] oldIds = profileScheduleTask.getResourceInstIds();
				long[] newIds = null;
				if (oldIds != null) {
					// 去掉实例重复
					HashSet<Long> set = new HashSet<>(resourceInstIds.length
							+ oldIds.length);
					for (long newInstanceId : resourceInstIds) {
						set.add(newInstanceId);
					}
					for (long oldInstanceId : oldIds) {
						set.add(oldInstanceId);
					}
					newIds = new long[set.size()];
					int i = 0;
					for (long intanceId : set) {
						newIds[i] = intanceId;
						i++;
					}
				} else {
					newIds = resourceInstIds;
				}
				Arrays.sort(newIds);
				profileScheduleTask.setResourceInstIds(newIds);

				/**
				 * 针对新增加的资源实例的指标，判断指标的下一次执行时间长度，时间长度超过10分钟级的指标，需要立即执行一次
				 */
				Date currentDate = new Date();
				long currentDateValue = currentDate.getTime();
				List<MetricScheduleTask> profileMetricTasks = getValidMetricScheduleTasks(
						profileScheduleTask, currentDateValue);
				// List<MetricScheduleTask> firedTasks = new ArrayList<>();
				for (MetricScheduleTask metricScheduleTask : profileMetricTasks) {
					if (metricScheduleTask.isActive()
							&& metricScheduleTask.getNextFireTime() != -1) {
						long offset = metricScheduleTask.getNextFireTime()
								- currentDateValue;
						if (offset >= offset_threshold) {
							// firedTasks.add(metricScheduleTask);
							metricScheduleTask
									.setNextFireTime(currentDateValue);
						}
					}
				}
				// if (firedTasks.size() > 0) {
				// FireTaskWrapper fireTaskWrapper = new FireTaskWrapper();
				// fireTaskWrapper.firedTasks = firedTasks;
				// fireTaskWrapper.resourceInstIds = resourceInstIds;
				// List<FireTaskWrapper> firedTaskswrapp = new ArrayList<>(1);
				// firedTaskswrapp.add(fireTaskWrapper);
				// fireTask(firedTaskswrapp, currentDate);
				// }
				synchronized (taskDriver) {
					taskDriver.notify();
				}
			} else {
				if (logger.isErrorEnabled()) {
					logger.error("schedule profile' task is not default profile.profileId="
							+ profileId);
				}
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("schedule profile' task not exist.profileId="
						+ profileId);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("schedule resourceInstIds end");
		}
	}

	@Override
	public synchronized void schedule(TimelineScheduleTask scheduleTask) {
		if (logger.isTraceEnabled()) {
			logger.trace("schedule TimelineScheduleTask start");
		}
		Long profileId = scheduleTask.getProfileId();
		if (profileScheduleTaskMap.containsKey(profileId)) {
			ProfileScheduleTask task = profileScheduleTaskMap.get(profileId);
			ProfileScheduleTask profileScheduleTask = (ProfileScheduleTask) task;
			Long timelineId = scheduleTask.getTimelineId();
			/**
			 * 该调度已经存在了
			 */
			if (timelineScheduleTaskMap.containsKey(timelineId)) {
				TimelineScheduleTask timelineTask = timelineScheduleTaskMap
						.remove(timelineId);
				profileScheduleTask.getTimelineScheduleTasks().remove(
						timelineTask);
				if (logger.isDebugEnabled()) {
					logger.debug("schedule updateTime timelineId=" + timelineId);
				}
			} else if (profileScheduleTask.getTimelineScheduleTasks() == null) {
				profileScheduleTask
						.setTimelineScheduleTasks(new ArrayList<TimelineScheduleTask>());
				if (logger.isDebugEnabled()) {
					logger.debug("schedule addTime timelineId=" + timelineId);
				}
			}
			timelineScheduleTaskMap.put(timelineId, scheduleTask);
			profileScheduleTask.getTimelineScheduleTasks().add(scheduleTask);
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("schedule TimelineScheduleTask profile' task not exist.profileId="
						+ profileId);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("schedule TimelineScheduleTask end");
		}
	}

	@Override
	public synchronized void removeScheduleByResourceInstanceIds(
			long[] resourceInstIds) {
		if (logger.isTraceEnabled()) {
			logger.trace("cancelSchedule resourceInstIds start");
		}
		Map<Long, ?> parameterIdsMap = new HashMap<>(resourceInstIds.length);
		for (int i = 0; i < resourceInstIds.length; i++) {
			parameterIdsMap.put(Long.valueOf(resourceInstIds[i]), null);
		}
		for (ProfileScheduleTask task : scheduleTasks) {
			long[] taskResourceInstIdList = task.getResourceInstIds();
			if (taskResourceInstIdList != null) {
				long[] taskResourceInstIdsTemp = new long[taskResourceInstIdList.length];
				int leaveLength = 0;
				for (int i = 0; i < taskResourceInstIdList.length; i++) {
					Long wrapperId = Long.valueOf(taskResourceInstIdList[i]);
					if (parameterIdsMap.containsKey(wrapperId)) {
						parameterIdsMap.remove(wrapperId);
						/**
						 * 父策略，需要删除对应的自定义指标
						 */
						if (task.getParentProfileId() <= 0) {
							List<MetricScheduleTask> metricScheduleTaskList = task
									.getTasks();
							if (metricScheduleTaskList != null
									&& metricScheduleTaskList.size() > 0) {
								for (Iterator<MetricScheduleTask> iterator = metricScheduleTaskList
										.iterator(); iterator.hasNext();) {
									MetricScheduleTask metricScheduleTask = (MetricScheduleTask) iterator
											.next();
									if (metricScheduleTask instanceof CustomMetricScheduleTask
											&& ((CustomMetricScheduleTask) metricScheduleTask)
													.getPluginParameter()
													.getInstanceId() == taskResourceInstIdList[i]) {
										if (logger.isInfoEnabled()) {
											StringBuilder b = new StringBuilder();
											b.append(
													"removeScheduleByResourceInstanceIds remove customMetric.InstanceId=")
													.append(b);
											b.append(" metricId=").append(
													taskResourceInstIdList[i]);
											logger.info(b.toString());
										}
										iterator.remove();
									}
								}
							}
						}
					} else {
						taskResourceInstIdsTemp[leaveLength++] = taskResourceInstIdList[i];
					}
				}

				if (leaveLength == 0) {
					task.setResourceInstIds(null);
				} else {
					long[] newIds = new long[leaveLength];
					System.arraycopy(taskResourceInstIdsTemp, 0, newIds, 0,
							leaveLength);
					Arrays.sort(newIds);
					task.setResourceInstIds(newIds);
				}
				taskResourceInstIdsTemp = null;
			}
			if (parameterIdsMap.size() <= 0) {
				// finish.
				break;
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("cancelSchedule resourceInstIds end");
		}
	}

	@Override
	public synchronized void cancelScheduleByProfileId(long profileId,
			String metricId) {
		if (logger.isTraceEnabled()) {
			logger.trace("cancelSchedule start");
		}
		if (profileScheduleTaskMap.containsKey(profileId)) {
			ScheduleTask task = profileScheduleTaskMap.get(profileId);
			List<MetricScheduleTask> metricScheduleTasks = task.getTasks();
			if (metricScheduleTasks != null && metricScheduleTasks.size() > 0) {
				for (Iterator<MetricScheduleTask> iterator = metricScheduleTasks
						.iterator(); iterator.hasNext();) {
					MetricScheduleTask metricScheduleTask = iterator.next();
					if (metricScheduleTask.getMetricId().equals(metricId)) {
						metricScheduleTask.setActive(false);
						break;
					}
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("cancelSchedule end");
		}
	}

	@Override
	public synchronized void removeSchedule(List<Long> profileIds) {
		if (logger.isTraceEnabled()) {
			logger.trace("cancelSchedule start profileIds=" + profileIds);
		}
		for (Long profileId : profileIds) {
			if (profileScheduleTaskMap.containsKey(profileId)) {
				ScheduleTask task = profileScheduleTaskMap.remove(profileId);
				if (task instanceof ProfileScheduleTask) {
					scheduleTasks.remove(task);
					ProfileScheduleTask pt = (ProfileScheduleTask) task;
					List<TimelineScheduleTask> ptTimeLineTasks = pt
							.getTimelineScheduleTasks();
					if (ptTimeLineTasks != null && ptTimeLineTasks.size() > 0) {
						for (TimelineScheduleTask timelineScheduleTask : ptTimeLineTasks) {
							timelineScheduleTaskMap.remove(timelineScheduleTask
									.getTimelineId());
						}
					}
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("cancelSchedule end");
		}
	}

	@Override
	public synchronized void updateTimeline(long timelineId,
			LimitedPeriodTimeFire limitedPeriodTimeFire) {
		if (logger.isTraceEnabled()) {
			logger.trace("updateTimeline start timelineId=" + timelineId);
		}
		Long wrappedTimelineId = timelineId;
		if (timelineScheduleTaskMap.containsKey(wrappedTimelineId)) {
			TimelineScheduleTask task = timelineScheduleTaskMap
					.get(wrappedTimelineId);
			task.setLimitedPeriodTimeFire(limitedPeriodTimeFire);
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("updateTimeline timeline id not found.timelineId="
						+ timelineId);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("updateTimeline end");
		}
	}

	@Override
	public synchronized void cancelScheduleByTimelineId(long timelineId,
			String metricId) {
		if (logger.isTraceEnabled()) {
			logger.trace("cancelScheduleByTimelineId start timelineId="
					+ timelineId + " metricId=" + metricId);
		}
		Long wrappedTimelineId = timelineId;
		if (timelineScheduleTaskMap.containsKey(wrappedTimelineId)) {
			TimelineScheduleTask task = timelineScheduleTaskMap
					.get(wrappedTimelineId);
			List<MetricScheduleTask> metricScheduleTasks = task.getTasks();
			if (metricScheduleTasks != null && metricScheduleTasks.size() > 0) {
				for (Iterator<MetricScheduleTask> iterator = metricScheduleTasks
						.iterator(); iterator.hasNext();) {
					MetricScheduleTask metricScheduleTask = iterator.next();
					if (metricScheduleTask.getMetricId().equals(metricId)) {
						metricScheduleTask.setActive(false);
						break;
					}
				}
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("cancelScheduleByTimelineId timeline id not found.timelineId="
						+ timelineId);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("cancelScheduleByTimelineId end");
		}
	}

	@Override
	public synchronized void removeScheduleByTimeline(long timelineId) {
		if (logger.isTraceEnabled()) {
			logger.trace("removeScheduleByTimeline start timelineId="
					+ timelineId);
		}
		Long wrappedTimelineId = timelineId;
		if (timelineScheduleTaskMap.containsKey(wrappedTimelineId)) {
			TimelineScheduleTask task = timelineScheduleTaskMap
					.remove(wrappedTimelineId);
			ProfileScheduleTask profileScheduleTask = profileScheduleTaskMap
					.get(task.getProfileId());
			if (profileScheduleTask != null) {
				boolean removeOk = false;
				if (profileScheduleTask.getTimelineScheduleTasks().size() > 0) {
					removeOk = profileScheduleTask.getTimelineScheduleTasks()
							.remove(task);
				}
				if (!removeOk) {
					if (logger.isErrorEnabled()) {
						logger.error("removeScheduleByTimeline timeline's profile not contains timeline obj.timelineId="
								+ timelineId
								+ " profileid="
								+ task.getProfileId());
					}
				}
			} else {
				if (logger.isErrorEnabled()) {
					logger.error("removeScheduleByTimeline timeline's profile not found.timelineId="
							+ timelineId + " profileid=" + task.getProfileId());
				}
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("removeScheduleByTimeline timeline id not found.timelineId="
						+ timelineId);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("removeScheduleByTimeline end");
		}
	}

	@Override
	public synchronized boolean hasProfileScheduleTask(long profileId) {
		if (logger.isTraceEnabled()) {
			logger.trace("hasProfileScheduleTask start profileId=" + profileId);
		}
		boolean contains = profileScheduleTaskMap.containsKey(profileId);
		if (logger.isTraceEnabled()) {
			logger.trace("hasProfileScheduleTask end contains=" + contains);
		}
		return contains;
	}

	@Override
	public synchronized void schedule(
			CustomMetricScheduleTask customMetricScheduleTask) {
		CustomMetricPluginParameter pluginParameter = customMetricScheduleTask
				.getPluginParameter();
		if (pluginParameter == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("schedule CustomMetricScheduleTask pluginParameter is empty.");
			}
			return;
		}
		if (pluginParameter.getCustomMetricId() == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("schedule CustomMetricScheduleTask CustomMetricId is empty.");
			}
			return;
		}
		if (pluginParameter.getInstanceId() <= 0) {
			if (logger.isWarnEnabled()) {
				logger.warn("schedule CustomMetricScheduleTask InstanceId is less than zero.InstanceId="
						+ pluginParameter.getInstanceId());
			}
			return;
		}
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("schedule CustomMetricScheduleTask start.InstanceId=")
					.append(pluginParameter.getInstanceId());
			b.append(" profileId=").append(
					customMetricScheduleTask.getProfileId());
			b.append(" metricId=").append(pluginParameter.getCustomMetricId());
			logger.info(b.toString());
		}
		if (customMetricScheduleTask.getProfileId() > 0) {
			/**
			 * 这里没有对策略中是否包含自定义指标task的资源实例进行验证。因为
			 * 
			 * 策略的发布是异步的，当资源实例加入监控的时候，有可能自定义指标首先
			 * 
			 * 发布到调度器，然后策略的资源实例加入动作后到达调度器。如果加入监控，取消监控，自定义指标发布的关系
			 * 
			 * 没有调用好，会造成同一个自定义指标出现在不同的策略里的情况。
			 */
			MetricScheduleTask task = customMetricScheduleTask;
			schedule(task);
		} else {
			long instanceId = pluginParameter.getInstanceId();
			List<ProfileScheduleTask> scheduleProfileTasks = new ArrayList<>(
					this.scheduleTasks);
			long profileId = -1;
			for (ProfileScheduleTask profileScheduleTask : scheduleProfileTasks) {
				long[] instanceIds = profileScheduleTask.getResourceInstIds();
				if (instanceIds != null
						&& Arrays.binarySearch(instanceIds, instanceId) >= 0) {
					profileId = profileScheduleTask.getProfileId();
					break;
				}
			}
			if (profileId >= 0) {
				MetricScheduleTask task = customMetricScheduleTask;
				task.setProfileId(profileId);
				schedule(task);
			} else {
				if (logger.isErrorEnabled()) {
					StringBuilder b = new StringBuilder();
					b.append(
							"schedule CustomMetricScheduleTask. no profile task was found.InstanceId=")
							.append(b);
					b.append(" metricId=").append(
							pluginParameter.getCustomMetricId());
					logger.error(b.toString());
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("schedule CustomMetricScheduleTask end");
		}
	}

	@Override
	public synchronized void removeCustomMetricScheduleTaskByInstanceId(
			long profileId, long instanceId, String metricId) {
		if (logger.isTraceEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append(
					"removeCustomMetricScheduleTaskByInstanceId start instanceId=")
					.append(instanceId);
			b.append(" profileId=").append(profileId);
			b.append(" metricId=").append(metricId);
			logger.trace(b.toString());
		}
		if (profileScheduleTaskMap.containsKey(profileId)) {
			ScheduleTask task = profileScheduleTaskMap.get(profileId);
			if (task instanceof ProfileScheduleTask) {
				List<MetricScheduleTask> metricScheduleTasks = task.getTasks();
				if (metricScheduleTasks != null
						&& metricScheduleTasks.size() > 0) {
					for (Iterator<MetricScheduleTask> iterator = metricScheduleTasks
							.iterator(); iterator.hasNext();) {
						MetricScheduleTask metricScheduleTask = iterator.next();
						if (metricScheduleTask instanceof CustomMetricScheduleTask
								&& metricScheduleTask.getMetricId().equals(
										metricId)
								&& ((CustomMetricScheduleTask) metricScheduleTask)
										.getPluginParameter().getInstanceId() == instanceId) {
							iterator.remove();
							if (logger.isInfoEnabled()) {
								StringBuilder b = new StringBuilder();
								b.append(
										"removeCustomMetricScheduleTaskByInstanceId ok.profileId=")
										.append(profileId);
								b.append(" instanceId=").append(instanceId);
								b.append(" metricId=").append(metricId);
								logger.info(b.toString());
							}
							break;
						}
					}
				}
			}
		} else {
			if (logger.isErrorEnabled()) {
				StringBuilder b = new StringBuilder();
				b.append(
						"schedule removeCustomMetricScheduleTaskByInstanceId. no profile task was found.InstanceId=")
						.append(instanceId);
				b.append(" profileId=").append(profileId);
				b.append(" metricId=").append(metricId);
				logger.error(b.toString());
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("removeCustomMetricScheduleTaskByInstanceId end");
		}
	}

	@Override
	public synchronized void removeMetricScheduleTaskByProfileId(
			long profileId, String metricId) {
		if (logger.isTraceEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("removeMetricScheduleTaskByProfileId start profileId=")
					.append(profileId);
			b.append(" metricId=").append(metricId);
			logger.trace(b.toString());
		}
		if (profileScheduleTaskMap.containsKey(profileId)) {
			ScheduleTask task = profileScheduleTaskMap.get(profileId);
			if (task instanceof ProfileScheduleTask) {
				ProfileScheduleTask pt = (ProfileScheduleTask) task;
				List<MetricScheduleTask> metricScheduleTasks = pt.getTasks();
				if (metricScheduleTasks != null
						&& metricScheduleTasks.size() > 0) {
					for (Iterator<MetricScheduleTask> iterator = metricScheduleTasks
							.iterator(); iterator.hasNext();) {
						MetricScheduleTask metricScheduleTask = iterator.next();
						if (metricScheduleTask.getMetricId().equals(metricId)) {
							iterator.remove();
							if (logger.isInfoEnabled()) {
								StringBuilder b = new StringBuilder();
								b.append(
										"removeMetricScheduleTaskByProfileId ok.profileId=")
										.append(profileId);
								b.append(" metricId=").append(metricId);
								logger.info(b.toString());
							}
							break;
						}
					}
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("removeMetricScheduleTaskByProfileId end");
		}
	}

	@Override
	public CustomMetricScheduleTask getCustomMetricScheduleTask(long profileId,
			long instanceId, String metricId) {
		if (logger.isTraceEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("getCustomMetricScheduleTask start profileId=").append(
					profileId);
			b.append(" instanceId=").append(instanceId);
			b.append(" metricId=").append(metricId);
			logger.trace(b.toString());
		}
		CustomMetricScheduleTask customMetricScheduleTask = null;
		if (profileScheduleTaskMap.containsKey(profileId)) {
			ScheduleTask task = profileScheduleTaskMap.get(profileId);
			if (task instanceof ProfileScheduleTask) {
				ProfileScheduleTask pt = (ProfileScheduleTask) task;
				List<MetricScheduleTask> metricScheduleTasks = pt.getTasks();
				if (metricScheduleTasks != null
						&& metricScheduleTasks.size() > 0) {
					for (Iterator<MetricScheduleTask> iterator = metricScheduleTasks
							.iterator(); iterator.hasNext();) {
						MetricScheduleTask metricScheduleTask = iterator.next();
						if (metricScheduleTask instanceof CustomMetricScheduleTask
								&& metricScheduleTask.getMetricId().equals(
										metricId)
								&& ((CustomMetricScheduleTask) metricScheduleTask)
										.getPluginParameter().getInstanceId() == instanceId) {
							customMetricScheduleTask = (CustomMetricScheduleTask) metricScheduleTask;
							if (logger.isDebugEnabled()) {
								StringBuilder b = new StringBuilder();
								b.append(
										"getCustomMetricScheduleTask ok.profileId=")
										.append(profileId);
								b.append(" instanceId=").append(instanceId);
								b.append(" metricId=").append(metricId);
								logger.debug(b.toString());
							}
							break;
						}
					}
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getCustomMetricScheduleTask end");
		}
		return customMetricScheduleTask;
	}

	@Override
	public synchronized void removeCustomMetricTasksByResourceInstanceIds(
			long[] resourceInstIds) {
		Map<Long, ?> parameterIdsMap = new HashMap<>(resourceInstIds.length);
		for (int i = 0; i < resourceInstIds.length; i++) {
			parameterIdsMap.put(Long.valueOf(resourceInstIds[i]), null);
		}
		for (ProfileScheduleTask task : scheduleTasks) {
			long[] taskResourceInstIdList = task.getResourceInstIds();
			if (taskResourceInstIdList != null) {
				for (int i = 0; i < taskResourceInstIdList.length; i++) {
					Long wrapperId = Long.valueOf(taskResourceInstIdList[i]);
					if (parameterIdsMap.containsKey(wrapperId)) {
						/**
						 * 父策略，需要删除对应的自定义指标
						 */
						if (task.getParentProfileId() <= 0) {
							List<MetricScheduleTask> metricScheduleTaskList = task
									.getTasks();
							if (metricScheduleTaskList != null
									&& metricScheduleTaskList.size() > 0) {
								for (Iterator<MetricScheduleTask> iterator = metricScheduleTaskList
										.iterator(); iterator.hasNext();) {
									MetricScheduleTask metricScheduleTask = (MetricScheduleTask) iterator
											.next();
									if (metricScheduleTask instanceof CustomMetricScheduleTask
											&& ((CustomMetricScheduleTask) metricScheduleTask)
													.getPluginParameter()
													.getInstanceId() == taskResourceInstIdList[i]) {
										if (logger.isInfoEnabled()) {
											StringBuilder b = new StringBuilder();
											b.append(
													"removeCustomMetricTasksByResourceInstanceIds remove customMetric.InstanceId=")
													.append(b);
											b.append(" metricId=").append(
													taskResourceInstIdList[i]);
											logger.info(b.toString());
										}
										iterator.remove();
									}
								}
							}
						}
						parameterIdsMap.remove(wrapperId);
					}
				}
			}
			if (parameterIdsMap.size() <= 0) {
				// finish.
				break;
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("cancelSchedule resourceInstIds end");
		}
	}

	@Override
	public synchronized List<CustomMetricScheduleTask> getCustomMetricScheduleTasksByMetricId(
			String metricId) {
		if (logger.isTraceEnabled()) {
			logger.trace("getCustomMetricScheduleTasksByMetricId start customMetricId="
					+ metricId);
		}
		List<CustomMetricScheduleTask> customMetricTasks = new ArrayList<>();
		for (ProfileScheduleTask task : scheduleTasks) {
			List<MetricScheduleTask> scheduleTasks = task.getTasks();
			if (scheduleTasks != null && scheduleTasks.size() > 0) {
				for (MetricScheduleTask metricScheduleTask : scheduleTasks) {
					if (metricScheduleTask instanceof CustomMetricScheduleTask
							&& metricScheduleTask.getMetricId()
									.equals(metricId)) {
						customMetricTasks
								.add((CustomMetricScheduleTask) metricScheduleTask);
					}
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getCustomMetricScheduleTasksByMetricId end");
		}
		return customMetricTasks;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		if (logger.isInfoEnabled()) {
			logger.info("start scheduler.");
		}
		this.start();
	}
}
