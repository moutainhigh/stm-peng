package com.mainsteam.stm.job;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class IJob {
	/**
	 * 唯一标示
	 */
	private String key = "";

	/**
	 * Job 类
	 */
	private Job job;

	/**
	 * Job 的动态参数
	 */
	private HashMap<String, Object> parameteMap;

	/**
	 * 调度表达式
	 */
	private Set<String> cronExpression = new HashSet<String>();

	/**
	 * Job 开始时间
	 */
	private Date startTime;

	/**
	 * Job 结束时间
	 */
	private Date endTime;

	public IJob(Job job, String cronExpression) {
		this.key = job.getClass().getName() + cronExpression.hashCode();
		this.job = job;
		this.cronExpression.add(cronExpression);
	}

	public IJob(String key, Job job, String cronExpression) {
		this.key = key;
		this.job = job;
		this.cronExpression.add(cronExpression);
	}

	public IJob(Job job, String cronExpression,
			HashMap<String, Object> parameteMap) {
		this.key = job.getClass().getName() + cronExpression.hashCode();
		this.job = job;
		this.cronExpression.add(cronExpression);
		this.parameteMap = parameteMap;
	}

	public IJob(String key, Job job, String cronExpression,
			HashMap<String, Object> parameteMap) {
		this.key = key;
		this.job = job;
		this.cronExpression.add(cronExpression);
		this.parameteMap = parameteMap;
	}

	public IJob(String key, Job job, Set<String> cronExpressions,
			HashMap<String, Object> parameteMap) {
		this.key = key;
		this.job = job;
		this.cronExpression = cronExpressions;
		this.parameteMap = parameteMap;
	}

	protected JobDetail getJobDetail() throws ClassNotFoundException {
		JobDataMap jm = new JobDataMap();
		if (parameteMap != null) {
			jm.putAll(parameteMap);
		}

		JobDetail jobDetail = null;
		JobBuilder jobBuilder = JobBuilder.newJob(job.getClass());
		// jobBuilder.withIdentity(key, job.getClass().getSimpleName());
		jobBuilder.withIdentity(key);
		jobBuilder.setJobData(jm);
		
		jobBuilder.storeDurably(true);// 没有trigger关联时自动删除
		jobBuilder.requestRecovery(true);// 自动恢复任务。
		
		jobDetail = jobBuilder.build();

		return jobDetail;
	}

	protected Trigger getTrigger() {
		getTriggers().iterator().hasNext();
		return getTriggers().iterator().next();
	}

	protected Set<Trigger> getTriggers() {
		Set<Trigger> sets = new HashSet<Trigger>();
		int i = 0;
		for (String ce : cronExpression) {
			CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder
					.cronSchedule(ce);
			TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder
					.newTrigger();
			// triggerBuilder.withIdentity(key, job.getClass().getSimpleName());
			triggerBuilder.withIdentity(key + (i++));
			triggerBuilder.withSchedule(cronScheduleBuilder);
			if (startTime != null) {
				triggerBuilder.startAt(startTime);
			}
			if (endTime != null) {
				triggerBuilder.endAt(endTime);
			}
			Trigger trigger = triggerBuilder.build();
			sets.add(trigger);
		}
		return sets;
	}

	public JobKey getJobKey() {
		JobKey jobKey = null;
		try {
			jobKey = getJobDetail().getKey();
		} catch (ClassNotFoundException e) {
		}
		return jobKey;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public String getCronExpression() {
		cronExpression.iterator().hasNext();
		return cronExpression.iterator().next();
	}

	public void addCronExpression(String cronExpression) {
		this.cronExpression.add(cronExpression);
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression.clear();
		this.cronExpression.add(cronExpression);
	}

	public HashMap<String, Object> getParameteMap() {
		return parameteMap;
	}

	public void setParameteMap(HashMap<String, Object> parameteMap) {
		this.parameteMap = parameteMap;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

}
