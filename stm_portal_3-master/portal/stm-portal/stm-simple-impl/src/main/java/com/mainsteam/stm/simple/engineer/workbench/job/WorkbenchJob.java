package com.mainsteam.stm.simple.engineer.workbench.job;

import org.apache.log4j.Logger;


import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.mainsteam.stm.simple.engineer.workbench.api.IWorkbenchApi;
import com.mainsteam.stm.util.SpringBeanUtil;

@DisallowConcurrentExecution
public class WorkbenchJob implements Job {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(WorkbenchJob.class);

	private IWorkbenchApi workbenchApi;
	public WorkbenchJob(){
		
	}
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		if (logger.isDebugEnabled()) {
			logger.debug("execute(JobExecutionContext) - start"); //$NON-NLS-1$
		}

		workbenchApi=SpringBeanUtil.getBean(IWorkbenchApi.class);
		if(workbenchApi != null){
			workbenchApi.updateCheckedAlarmEvemtIsRecovered();
		}else{
			logger.error("WorkbenchJob workbenchApi is null");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("execute(JobExecutionContext) - end"); //$NON-NLS-1$
		}
	}
	
}
