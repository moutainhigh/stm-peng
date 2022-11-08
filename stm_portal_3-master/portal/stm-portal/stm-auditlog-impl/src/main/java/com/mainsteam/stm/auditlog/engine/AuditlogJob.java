package com.mainsteam.stm.auditlog.engine;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.mainsteam.stm.auditlog.bo.AuditlogTemplet;

public class AuditlogJob implements Job {
	private  final Log logger = LogFactory.getLog(AuditlogJob.class);

	@Override
	public void execute(JobExecutionContext jobExec) throws JobExecutionException {
		
		logger.debug("Into auditlogjob execute!! time : " + new Date(System.currentTimeMillis()));
		
		JobDataMap jobDataMap=jobExec.getJobDetail().getJobDataMap();
		
		//获取Job的参数
		AuditlogTemplet auditlogTemplet=(AuditlogTemplet)jobDataMap.get("AUDITLOG_TEMPLATE");
		
		logger.debug("Get jobDataMap auditlogTemplet,auditlogTempletID = " + auditlogTemplet.getAuditlogTempletId());
		
		AuditlogTask auditlogTask=new AuditlogTask();
		try {
			//Job 任务开始
			logger.debug("Start auditlogTask!!");
			auditlogTask.start();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}
}
