package com.mainsteam.stm.ct.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.mainsteam.stm.ct.api.IProbeTaskApi;
import com.mainsteam.stm.util.SpringBeanUtil;

@DisallowConcurrentExecution
@Component
public class AverageWebJob implements Job{
	private  final Log logger = LogFactory.getLog(AverageWebJob.class);
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		try {
			
			AverageWebTask task = SpringBeanUtil.getBean(AverageWebTask.class);
			if(null != task){
				task.averageWebVal();
			}
		} catch (Exception e) {
			logger.error("averageWebVal发生异常!",e);
		}
	}
}
