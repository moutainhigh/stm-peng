/**
 * 
 */
package com.mainsteam.stm.job;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;

/**
 * @author ziw
 * 
 */
public class ScheduleJobFilter extends TriggerListenerSupport {

	private Map<String, Boolean> jobRunnerRegisterMap;

	/**
	 * 
	 */
	public ScheduleJobFilter() {
		jobRunnerRegisterMap = new HashMap<String, Boolean>();
	}

	public void registerJob(IJob job) {
		jobRunnerRegisterMap.put(job.getKey(), Boolean.TRUE);
	}

	public void unregisterJob(IJob job) {
		jobRunnerRegisterMap.remove(job.getKey());
	}
	
	public void unregisterJob(String jobKey) {
		jobRunnerRegisterMap.remove(jobKey);
	}
	
	public void clear(){
		jobRunnerRegisterMap.clear();
	}

	@Override
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
		String key = trigger.getJobKey().getName();
		System.out.println("run key="+key);
		if (jobRunnerRegisterMap.containsKey(key)
				&& jobRunnerRegisterMap.get(key).booleanValue()) {
			return false;
		}
		return true;
	}

	@Override
	public String getName() {
		return "OC4_ScheduleJobFilter";
	}
}
