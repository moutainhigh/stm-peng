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
public class SingleResourceJob implements Job{
	private  final Log logger = LogFactory.getLog(SingleResourceJob.class);
	
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		try {
			
			SingleResourceMetricTask task = SpringBeanUtil.getBean(SingleResourceMetricTask.class);
			if(null != task){
				task.singleResourceVal();
			}
		} catch (Exception e) {
			logger.error("SingleResourceMetricTask发生异常!",e);
		}
	}
}