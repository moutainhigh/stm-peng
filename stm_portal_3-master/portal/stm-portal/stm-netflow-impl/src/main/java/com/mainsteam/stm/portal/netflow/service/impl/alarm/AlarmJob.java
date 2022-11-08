package com.mainsteam.stm.portal.netflow.service.impl.alarm;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.mainsteam.stm.util.SpringBeanUtil;



public class AlarmJob implements Job{
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		AlarmHandler alarmHandler = (AlarmHandler) SpringBeanUtil.getBean(AlarmHandler.class);
		alarmHandler.sendAlarm();
	}
}
