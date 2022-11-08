package com.mainsteam.stm.camera.impl;

import java.util.HashMap;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.job.IJob;
import com.mainsteam.stm.job.ScheduleManager;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;

public class CameraReportEngine {

	 private final Log logger = LogFactory.getLog(CameraReportEngine.class);
	
	
	 		@Resource
	 private ScheduleManager scheduleManager;
	
	 private static final String REPORT_KEY = "Camera-Report-Job-";
	
	 public void startEngine(ReportTemplate reportTemplate)
			 throws ClassNotFoundException, SchedulerException, InstancelibException
	 {
		 String jobKey = REPORT_KEY + reportTemplate.getReportTemplateId();
		
	 boolean isScheduleJob = reportTemplate.getReportTemplateStatus() == 0;
		
		 if (!(isScheduleJob)) {
			 this.logger.info("===================Job is not init,you can open it on report template pages.");
			if (this.scheduleManager.isExists(jobKey)) {
			 this.logger.warn("=====================Stop Job===================");
				 this.logger.warn("=====================Stop Job By Key:" + jobKey);
				 this.logger.warn("===============================================");
				 this.scheduleManager.deleteJob(jobKey);
				 }
			 return;
			}
		
		 CameraReportJob reportJob = new CameraReportJob();
		 String cronExpression = getCronExpression(reportTemplate);
		 this.logger.info("===================Job Start===================");
		 this.logger.info("Report Job [" + jobKey + "] Start:" + cronExpression);
		 this.logger.info("===============================================");
		
		 HashMap dateMap = new HashMap();
		 dateMap.put("REPORT_TEMPLATE", reportTemplate);
		
		 IJob iReportJob = new IJob(jobKey, reportJob, cronExpression, dateMap);
		
	this.scheduleManager.scheduleJob(iReportJob);
	}
	
	 public void stopEngine(long reportTemplateId)/*     */ throws ClassNotFoundException, SchedulerException
	 {
		 String jobKey = REPORT_KEY + reportTemplateId;
	 this.scheduleManager.deleteJob(jobKey);
		
		 this.logger.warn("=====================Delete Job================");
		 this.logger.warn("=====================Delete Job By Key:" + jobKey);
		 this.logger.warn("===============================================");
		}
	
public void updateEngine(ReportTemplate reportTemplate)
		throws ClassNotFoundException, SchedulerException, InstancelibException
	 {
		 CameraReportJob reportJob = new CameraReportJob();
		
		 String cronExpression = getCronExpression(reportTemplate);
		
		 String jobKey = REPORT_KEY + reportTemplate.getReportTemplateId();
		
		this.logger.info("==================Update Job===================");
		 this.logger.info("Report Job [" + jobKey + "] Update:" + cronExpression);
	 this.logger.info("===============================================");
		
		HashMap dateMap = new HashMap();
		 dateMap.put("REPORT_TEMPLATE", reportTemplate);
		
		 IJob iReportJob = new IJob(jobKey, reportJob, cronExpression, dateMap);
		
		 this.scheduleManager.updateJob(jobKey, iReportJob);
	 }
	
	 public String getCronExpression(ReportTemplate reportTemplate)
	{
		 String cronExpression = "";
		
		 int cycle = reportTemplate.getReportTemplateCycle();
		
		int reportType = reportTemplate.getReportTemplateType();
		
		 if (reportType == 5) {
			 if (cycle == 2)/* 136 */ cronExpression = "0 0 8 ? * MON";
			 else if (cycle == 3) {
			 cronExpression = "0 0 8 1 * ?";
			 }
			 return cronExpression;
			 }
		
		 if (reportType == 6) {
		 if (cycle == 1)/* 145 */ cronExpression = "0 0 8 * * ?";
			 else if (cycle == 2)/* 147 */ cronExpression = "0 0 8 ? * MON";
			else if (cycle == 3) {
				 cronExpression = "0 0 8 1 * ?";
			 }
		
		 return cronExpression;
			 }
		
		 int second = reportTemplate.getReportTemplateSecondGenerateTime();
		 int third = reportTemplate.getReportTemplateThirdGenerateTime();
		
		 String dayOfWeek = "";
		 switch (second)
		{
			 case 1 :
				 dayOfWeek = "MON";
				 break;
			 case 2 :
				 dayOfWeek = "TUE";
			 break;
			 case 3 :
			dayOfWeek = "WED";
				break;
			 case 4 :
			dayOfWeek = "THU";
				break;
			case 5 :
			dayOfWeek = "FRI";
			break;
			 case 6 :
			dayOfWeek = "SAT";
				break;
			case 7 :
				 dayOfWeek = "SUN";
				 }
		
		 if (cycle == 1) {
			 if (second == 24)
			 {
				second = 0;
				 }
			 cronExpression = "0 0 " + second + " * * ?";
			 } else if (cycle == 2) {
			 if (third == 24)
			 {
				 third = 0;
				 }
			cronExpression = "0 0 " + third + " ? * " + dayOfWeek;
			 } else {
			 if (third == 24)
			 {
				 third = 0;
			 }
			 if (second == -1)
				 cronExpression = "0 0 " + third + " L * ?";
			 else {
				 cronExpression = "0 0 " + third + " " + second + " * ?";
				 }
			}
		
		 return cronExpression;
		}

}
