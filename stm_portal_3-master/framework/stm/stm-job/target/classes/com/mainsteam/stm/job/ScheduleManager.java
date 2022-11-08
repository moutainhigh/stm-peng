package com.mainsteam.stm.job;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

public class ScheduleManager {

	private static final Log logger = LogFactory.getLog(ScheduleManager.class);

	private Scheduler scheduler;

	// private ScheduleJobFilter scheduleJobFilter;

	/**
	 * 执行一个Job
	 * 
	 * @param job
	 * @throws ClassNotFoundException
	 * @throws SchedulerException
	 */
	public void scheduleJob(IJob job) throws ClassNotFoundException,
			SchedulerException {
		// scheduleJobFilter.registerJob(job);
		Set<Trigger> triggers = job.getTriggers();
		if (!isExists(job)) {
			// scheduler.scheduleJob(job.getJobDetail(), job.getTrigger());
			scheduler.scheduleJob(job.getJobDetail(), triggers, true);
		} else {
			validateAndRecoverBlockedJob(job, triggers);
		}
	}

	private void validateAndRecoverBlockedJob(IJob job, Set<Trigger> triggers) {
		for (Trigger trigger : triggers) {
			try {
				Trigger.TriggerState state = scheduler.getTriggerState(trigger
						.getKey());
				if (state == null) {
					continue;
				}
				if (state.equals(Trigger.TriggerState.BLOCKED)) {
					Trigger dbTrigger = scheduler.getTrigger(trigger.getKey());
					Date nextFireTime = dbTrigger.getNextFireTime();
					long offsetThreshold = 60000;// 1minute.
					Date currentDate = new Date();
					/* more than 1 minute,rescheduleJob. */
					if (currentDate.getTime() - nextFireTime.getTime() > offsetThreshold) {
						scheduler.resumeTrigger(trigger.getKey());
						scheduler.rescheduleJob(trigger.getKey(), trigger);
						if (logger.isInfoEnabled()) {
							logger.info("validateAndRecoverBlockedJob reschedule trigger.");
						}
					}
				} else if (state.equals(Trigger.TriggerState.NONE)) {
					scheduler.resumeTrigger(trigger.getKey());
					scheduler.rescheduleJob(trigger.getKey(), trigger);
				}
			} catch (SchedulerException e) {
				if (logger.isErrorEnabled()) {
					logger.error("recoverBlockedJob", e);
				}
			}
		}
	}

	/**
	 * 删除一个Job
	 * 
	 * @param job
	 * @throws ClassNotFoundException
	 * @throws SchedulerException
	 */
	public void deleteJob(IJob job) throws ClassNotFoundException,
			SchedulerException {
		if(isExists(job)){
			scheduler.deleteJob(job.getJobKey());
		}
		// scheduleJobFilter.unregisterJob(job);
	}

	/**
	 * 删除一个Job
	 * 
	 * @param jobKey
	 * @throws ClassNotFoundException
	 * @throws SchedulerException
	 */
	public void deleteJob(JobKey jobKey) throws ClassNotFoundException,
			SchedulerException {
		if(isExists(jobKey.getName())){
			scheduler.deleteJob(jobKey);
		}
		// scheduleJobFilter.unregisterJob(jobKey.getName());
	}

	public void deleteJob(String key) throws ClassNotFoundException,
			SchedulerException {
		if(isExists(key)){
			scheduler.deleteJob(new JobKey(key));
		}
		// scheduleJobFilter.unregisterJob(key);
	}

	public void updateJob(String jobKey, IJob job)
			throws ClassNotFoundException, SchedulerException {
		deleteJob(jobKey);
		scheduleJob(job);
	}

	public boolean isExists(String jobKey) throws SchedulerException {
		return scheduler.checkExists(new JobKey(jobKey));
	}

	public boolean isExists(IJob job) throws SchedulerException {
		return scheduler.checkExists(job.getJobKey());
	}

	/**
	 * 清理 Jobs
	 * 
	 * @throws SchedulerException
	 */
	protected void cleanJobs() throws SchedulerException {
		scheduler.clear();
	}

	/**
	 * 暂停 All Jobs
	 * 
	 * @throws SchedulerException
	 */
	protected void pauseAllJobs() throws SchedulerException {
		scheduler.pauseAll();
	}

	/**
	 * 恢复 All Jobs
	 * 
	 * @throws SchedulerException
	 */
	protected void resumeAllJobs() throws SchedulerException {
		scheduler.resumeAll();
	}

	protected void start() throws SchedulerException {
		// scheduleJobFilter = new ScheduleJobFilter();
		// System.out.println("add trigger filter.");
		// scheduler.getListenerManager().addTriggerListener(scheduleJobFilter);
		// scheduler.getListenerManager().addSchedulerListener(
		// new JobFilterSchedulerListener(scheduleJobFilter));
		scheduler.start();
	}

	protected void shutdown() throws SchedulerException {
		scheduler.shutdown();
	}

	// /**
	// * structure
	// */
	// private static ScheduleManager scheduleManager;
	// private ScheduleManager(){}
	// public static ScheduleManager getInstance() throws SchedulerException{
	// if(scheduleManager==null){
	// synchronized (ScheduleManager.class) {
	// if(scheduleManager==null){
	// scheduler=getScheduler();
	// scheduleManager = new ScheduleManager();
	// }
	// }
	// }
	// return scheduleManager;
	// }
	//
	// /**
	// * 创建一个调度对象
	// * @return
	// * @throws SchedulerException
	// */
	// private static Scheduler getScheduler() throws SchedulerException {
	// SchedulerFactory sf = new StdSchedulerFactory();
	// return sf.getScheduler();
	// }

	/**
	 * add jobs
	 * 
	 * @param triggersAndJobs
	 * @param replace
	 * @throws SchedulerException
	 * @throws ClassNotFoundException
	 */
	public void addJobs(List<IJob> tasks) throws SchedulerException,
			ClassNotFoundException {
		addJobs(tasks, false);
	}

	/**
	 * add jobs
	 * 
	 * @param triggersAndJobs
	 * @param replace
	 * @throws SchedulerException
	 * @throws ClassNotFoundException
	 */
	public void addJobs(List<IJob> jobs, boolean replace)
			throws SchedulerException, ClassNotFoundException {
		Map<JobDetail, Set<? extends Trigger>> triggersAndJobs = new HashMap<JobDetail, Set<? extends Trigger>>();
		for (IJob job : jobs) {
			JobDetail jobDetal = job.getJobDetail();
			Trigger trigger = job.getTrigger();
			Set<Trigger> set = new HashSet<Trigger>();
			set.add(trigger);
			triggersAndJobs.put(jobDetal, set);
			// scheduleJobFilter.registerJob(job);
		}
		scheduler.scheduleJobs(triggersAndJobs, replace);
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

}
