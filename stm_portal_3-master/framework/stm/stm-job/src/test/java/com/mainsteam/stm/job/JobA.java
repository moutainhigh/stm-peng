package com.mainsteam.stm.job;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.mainsteam.stm.util.SpringBeanUtil;

public class JobA implements Job{


	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		  
//		Object datasource = SpringBeanUtil.getObject("defaultDataSource");
		
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		System.out.println("========Do Job AAAA========="+simpleDateFormat.format(new Date(System.currentTimeMillis())));
		
//		System.out.println("datasource="+datasource);
	}
	
}
