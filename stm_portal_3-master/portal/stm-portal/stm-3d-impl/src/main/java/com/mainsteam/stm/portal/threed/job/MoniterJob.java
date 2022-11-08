package com.mainsteam.stm.portal.threed.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class MoniterJob implements Job {
	/**
	 * 日志
	 */
	private final Log logger = LogFactory.getLog(MoniterJob.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			MoniterTask task = new MoniterTask();
			task.start();
		} catch (Exception e) {
			logger.error("推送3D监控数据异常,原因:" + e.getMessage(), e);
		}

	}

}
