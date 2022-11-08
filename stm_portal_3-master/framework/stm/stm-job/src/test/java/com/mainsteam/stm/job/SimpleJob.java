package com.mainsteam.stm.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SimpleJob implements Job{
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("run test.");
		Counter.count++;
	}
}
