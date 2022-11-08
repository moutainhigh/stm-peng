package com.mainsteam.stm.camera.impl;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.mainsteam.stm.portal.report.bo.ReportTemplate;
import com.mainsteam.stm.portal.report.engine.ReportTask;

public class CameraReportJob  implements Job {
	
	private final Log logger;
	
	public CameraReportJob(){
		 this.logger = LogFactory.getLog(CameraReportJob.class);
	}
	
	    public void execute(JobExecutionContext jobExec) throws JobExecutionException
	   {
	   this.logger.debug("Into reportJob execute!! time : " + new Date(System.currentTimeMillis()));
	  
	     JobDataMap jobDataMap = jobExec.getJobDetail().getJobDataMap();
	   
	   
	       ReportTemplate reportTemplate = (ReportTemplate)jobDataMap.get("REPORT_TEMPLATE");

	      this.logger.debug("Get jobDataMap reportTemplate,reportTemplateID = " + reportTemplate.getReportTemplateId());
	  
	      CameraReportTask reportTask = new CameraReportTask(reportTemplate.getReportTemplateId());
	       try
	       {
	       this.logger.debug("Start reportTask!!");
	       reportTask.start();
	     } catch (Exception e) {
	       this.logger.error(e.getMessage(), e);
	        this.logger.error(e.getStackTrace());
	    }
	    }

}
