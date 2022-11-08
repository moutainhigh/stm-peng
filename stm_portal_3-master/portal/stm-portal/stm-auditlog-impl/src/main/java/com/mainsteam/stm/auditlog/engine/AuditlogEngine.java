package com.mainsteam.stm.auditlog.engine;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.job.IJob;
import com.mainsteam.stm.job.ScheduleManager;
import com.mainsteam.stm.auditlog.bo.AuditlogTemplet;

public class AuditlogEngine {
	
	private  final Log logger = LogFactory.getLog(AuditlogEngine.class);
	
	private ScheduleManager scheduleManager;
	
	private static final String AUDITLOG_KEY="Auditlog-Job-";
	
	public AuditlogEngine() {
		// TODO Auto-generated constructor stub
	}
	public void startEngine(AuditlogTemplet auditlogTemplet) throws ClassNotFoundException, SchedulerException, InstancelibException{
		String jobKey=AUDITLOG_KEY+auditlogTemplet.getAuditlogTempletId();
		
		//是否启用:0.启用1.停用
		boolean isScheduleJob = auditlogTemplet.getAuditlogTempletStatus().equals("0");
		
		if(!isScheduleJob){
			logger.info("===================Job is not init,you can open it on Auditlog template pages.");
			if(scheduleManager.isExists(jobKey)){
				logger.warn("=====================Stop Job===================");
				logger.warn("=====================Stop Job By Key:"+jobKey);
				logger.warn("===============================================");
				scheduleManager.deleteJob(jobKey);
			}
			return ;
		}
		
		//make Job
		AuditlogJob auditlogJob=new AuditlogJob();
		String cronExpression=getCronExpression(auditlogTemplet);
		logger.info("===================Job Start===================");
		logger.info("Report Job ["+jobKey+"] Start:"+cronExpression);
		logger.info("===============================================");
				
		//Job parameter
		HashMap<String,Object> dateMap = new HashMap<String,Object>();
		dateMap.put("AUDITLOG_TEMPLATE", auditlogTemplet);
				
		IJob iAuditlogJob=new IJob(jobKey,auditlogJob,cronExpression,dateMap);
		//调度Job
		this.scheduleManager.scheduleJob(iAuditlogJob);
		
	}
	
	public void stopEngine(long auditlogTemplateId) throws ClassNotFoundException, SchedulerException{
		String jobKey=AUDITLOG_KEY+auditlogTemplateId;
		scheduleManager.deleteJob(jobKey);
		
		logger.warn("=====================Delete Job================");
		logger.warn("=====================Delete Job By Key:"+jobKey);
		logger.warn("===============================================");
	}
	
	public void updateEngine(AuditlogTemplet auditlogTemplet) throws ClassNotFoundException, SchedulerException, InstancelibException{
		//make Job
		AuditlogJob reportJob=new AuditlogJob();

		String cronExpression=getCronExpression(auditlogTemplet);
		
		String jobKey=AUDITLOG_KEY+auditlogTemplet.getAuditlogTempletId();
		
		logger.info("==================Update Job===================");
		logger.info("Report Job ["+jobKey+"] Update:"+cronExpression);
		logger.info("===============================================");
		
		//Job parameter
		HashMap<String,Object> dateMap = new HashMap<String,Object>();
		dateMap.put("AUDITLOG_TEMPLATE", auditlogTemplet);
		
		IJob iAuditlogJob=new IJob(jobKey,reportJob,cronExpression,dateMap);
		//调度Job
		this.scheduleManager.updateJob(jobKey, iAuditlogJob);
	}

	
	public String getCronExpression(AuditlogTemplet auditlogTemplet){
		String cronExpression="";
		int minute = Integer.parseInt(auditlogTemplet.getAuditlogTempletMinute());
		int hour = Integer.parseInt(auditlogTemplet.getAuditlogTempletHour());
		cronExpression = "0 "+minute+" "+hour+" */"+auditlogTemplet.getAuditlogTempletDay()+" * ?";
		return cronExpression;
	}
	public ScheduleManager getScheduleManager() {
		return scheduleManager;
	}
	public void setScheduleManager(ScheduleManager scheduleManager) {
		this.scheduleManager = scheduleManager;
	}
	
	
}
