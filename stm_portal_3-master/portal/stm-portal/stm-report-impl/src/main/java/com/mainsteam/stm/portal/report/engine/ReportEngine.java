package com.mainsteam.stm.portal.report.engine;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.job.IJob;
import com.mainsteam.stm.job.ScheduleManager;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;

/**
* <p>报表引擎，用于定时生成报表</p>
* <li>文件名称: ReportEngine.java</li>
* <li>公　　司: 武汉美新翔盛科技有限公司</li>
* <li>版权所有: 版权所有(C)2019-2020</li>
* <li>修改记录: ...</li>
* <li>内容摘要: ...</li>
* <li>其他说明: ...</li>
* @version  ms.stm
* @since    2019年10月16日
* @author   WangXinghao
* @tags
 */
public class ReportEngine {
	
	private  final Log logger = LogFactory.getLog(ReportEngine.class);
	
	private ScheduleManager scheduleManager;

	private static final String REPORT_KEY="Report-Job-";
	
	public ReportEngine(){}
	
	/**
	 * 启动报表引起 定时产生报表数据
	 * @param reportTemplate
	 * @throws ClassNotFoundException
	 * @throws SchedulerException
	 * @throws InstancelibException 
	 */
	public void startEngine(ReportTemplate reportTemplate) throws ClassNotFoundException, SchedulerException, InstancelibException{
		String jobKey=REPORT_KEY+reportTemplate.getReportTemplateId();
		
		//是否启用:0.启用1.停用
		boolean isScheduleJob=reportTemplate.getReportTemplateStatus()==0;
		
		if(!isScheduleJob){
			logger.info("===================Job is not init,you can open it on report template pages.");
			if(scheduleManager.isExists(jobKey)){
				logger.warn("=====================Stop Job===================");
				logger.warn("=====================Stop Job By Key:"+jobKey);
				logger.warn("===============================================");
				scheduleManager.deleteJob(jobKey);
			}
			return ;
		}
		
		//make Job
		ReportJob reportJob=new ReportJob();
		String cronExpression=getCronExpression(reportTemplate);
		logger.info("===================Job Start===================");
		logger.info("Report Job ["+jobKey+"] Start:"+cronExpression);
		logger.info("===============================================");
		
		//Job parameter
		HashMap<String,Object> dateMap = new HashMap<String,Object>();
		dateMap.put("REPORT_TEMPLATE", reportTemplate);
		
		IJob iReportJob=new IJob(jobKey,reportJob,cronExpression,dateMap);
		//调度Job
		this.scheduleManager.scheduleJob(iReportJob);
	}
	
	/**
	 * 停止报表引起
	 * @param reportTemplateId
	 * @throws ClassNotFoundException
	 * @throws SchedulerException
	 */
	public void stopEngine(long reportTemplateId) throws ClassNotFoundException, SchedulerException{
		String jobKey=REPORT_KEY+reportTemplateId;
		scheduleManager.deleteJob(jobKey);
		
		logger.warn("=====================Delete Job================");
		logger.warn("=====================Delete Job By Key:"+jobKey);
		logger.warn("===============================================");
	}

	/**
	 * 更新Job
	 * @param reportTemplate
	 * @throws ClassNotFoundException
	 * @throws SchedulerException
	 * @throws InstancelibException
	 */
	public void updateEngine(ReportTemplate reportTemplate) throws ClassNotFoundException, SchedulerException, InstancelibException{
		//make Job
		ReportJob reportJob=new ReportJob();

		String cronExpression=getCronExpression(reportTemplate);
		
		String jobKey=REPORT_KEY+reportTemplate.getReportTemplateId();
		
		logger.info("==================Update Job===================");
		logger.info("Report Job ["+jobKey+"] Update:"+cronExpression);
		logger.info("===============================================");
		
		//Job parameter
		HashMap<String,Object> dateMap = new HashMap<String,Object>();
		dateMap.put("REPORT_TEMPLATE", reportTemplate);
		
		IJob iReportJob=new IJob(jobKey,reportJob,cronExpression,dateMap);
		//调度Job
		this.scheduleManager.updateJob(jobKey, iReportJob);
	}
	
	/**
	 * 通过模板获取引起的启动时间
	 * @param reportTemplate
	 * @return
	 */
	public String getCronExpression(ReportTemplate reportTemplate){
		
		String cronExpression="";
		
		//报表周期：1.日报2.周报3.月报
		int cycle=reportTemplate.getReportTemplateCycle();
		
		int reportType=reportTemplate.getReportTemplateType();
		//趋势 : 下周 周一 08:00 / 下月 1号 08:00
		if(reportType==5){
			if(cycle==2){
				cronExpression="0 0 8 ? * MON";
			}else if(cycle==3){
				cronExpression="0 0 8 1 * ?" ;
			}
			return cronExpression;
		}
		//分析 :次日 08:00 / 下周一 08:00 /下月 1号 08:00
		if(reportType==6){
			if(cycle==1){
				cronExpression="0 0 8 * * ?";
			}else if(cycle==2){
				cronExpression="0 0 8 ? * MON";
			}else if(cycle==3){
				cronExpression="0 0 8 1 * ?" ;
			}
			
			return cronExpression;
		}
		


		int second=reportTemplate.getReportTemplateSecondGenerateTime();
		int third=reportTemplate.getReportTemplateThirdGenerateTime();
		
		//前台定义的   1:星期一
		String dayOfWeek = "";
		switch(second){
			case 1:
				dayOfWeek = "MON";
				break;
			case 2:
				dayOfWeek = "TUE";
				break;
			case 3:
				dayOfWeek = "WED";
				break;
			case 4:
				dayOfWeek = "THU";
				break;
			case 5:
				dayOfWeek = "FRI";
				break;
			case 6:
				dayOfWeek = "SAT";
				break;
			case 7:
				dayOfWeek = "SUN";
				break;
		}
		
		if(cycle==1){
			if(second == 24){
				//cron表达式不支持24
				second = 0;
			}
			cronExpression= "0 0 "+second+" * * ?";
		}else if(cycle==2){
			if(third == 24){
				//cron表达式不支持24
				third = 0;
			}
			cronExpression= "0 0 "+third+" ? * "+dayOfWeek;
		}else{
			if(third == 24){
				//cron表达式不支持24
				third = 0;
			}
			if(second==-1){
				cronExpression= "0 0 "+third+" L * ?";
			}else{
				cronExpression= "0 0 "+third+" "+second+" * ?";
			}
		}
		
		return cronExpression;
	}
	
	public ScheduleManager getScheduleManager() {
		return scheduleManager;
	}

	public void setScheduleManager(ScheduleManager scheduleManager) {
		this.scheduleManager = scheduleManager;
	}
	
	
}
