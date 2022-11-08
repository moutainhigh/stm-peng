package com.mainsteam.stm.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class JobB implements Job{

	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		  
		System.out.println("========Do Job BBB=========");
		
	}
	
}
