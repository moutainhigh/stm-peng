package com.mainsteam.stm.portal.report.engine;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.mainsteam.stm.portal.report.bo.ReportTemplate;

/**
 * 
* <li>文件名称: ReportJob.java</li>
* <li>公　　司: 武汉美新翔盛科技有限公司</li>
* <li>版权所有: 版权所有(C)2019-2020</li>
* <li>修改记录: ...</li>
* <li>内容摘要: ...</li>
* <li>其他说明: ...</li>
* @version  ms.stm
* @since    2019年10月22日
* @author   Wang
* @tags
 */
@DisallowConcurrentExecution
public class ReportJob implements Job {

	private  final Log logger = LogFactory.getLog(ReportJob.class);

	@Override
	public void execute(JobExecutionContext jobExec) throws JobExecutionException {
		
		logger.debug("Into reportJob execute!! time : " + new Date(System.currentTimeMillis()));
		
		JobDataMap jobDataMap=jobExec.getJobDetail().getJobDataMap();
		
		//获取Job的参数
		ReportTemplate reportTemplate=(ReportTemplate)jobDataMap.get("REPORT_TEMPLATE");
		
		logger.debug("Get jobDataMap reportTemplate,reportTemplateID = " + reportTemplate.getReportTemplateId());
		
		ReportTask reportTask=new ReportTask(reportTemplate.getReportTemplateId());
		try {
			//Job 任务开始，创建XML数据文件
			logger.debug("Start reportTask!!");
			reportTask.start();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error(e.getStackTrace());
		}

	}

}
