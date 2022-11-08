package com.mainsteam.stm.portal.config.job;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.job.IJob;
import com.mainsteam.stm.job.ScheduleManager;
import com.mainsteam.stm.portal.config.bo.BackupPlanBo;
import com.mainsteam.stm.util.DateUtil;

/**
 * <li>文件名称: ConfigEngine.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月28日
 * @author   caoyong
 */
public class ConfigEngine {
	/**
	 * 日志
	 */
	private  final Log logger = LogFactory.getLog(ConfigEngine.class);
	/**
	 * job计划Manager
	 */
	private ScheduleManager scheduleManager;
	/**
	 * job key
	 */
	private static final String JOB_KEY = "Configfilemanage-Job-";
	
	public ConfigEngine(){}
	/**
	 * 启动job任务
	 * @param
	 */
	public void startJob(BackupPlanBo plan) throws ClassNotFoundException, SchedulerException, InstancelibException{
		IJob job = creatJob(plan);
		//调度Job
		this.scheduleManager.scheduleJob(job);
	}
	
	public void updateJob(BackupPlanBo plan) throws ClassNotFoundException, SchedulerException{
		String jobKey=JOB_KEY+plan.getId();
		IJob job = creatJob(plan);
		this.scheduleManager.updateJob(jobKey, job);
//		if(this.scheduleManager.isExists(job)){
//			this.scheduleManager.updateJob(jobKey, job);
//		}else{
//			//调度Job
//			this.scheduleManager.scheduleJob(job);
//		}
	}	
	
	private IJob creatJob(BackupPlanBo plan){
		ConfigJob configJob=new ConfigJob();
		String cronExpression=getCronExpression(plan);
		//jobkey=JOB_KEY+备份计划ID
		String jobKey=JOB_KEY+plan.getId();
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("plan", plan);
		logger.info("configfilemanage Job ["+jobKey+"] Start"+cronExpression);
		//运行周期（测试用），根据  0 0/10 * * * ?
		//cronExpression="0 */1 * * * ?";
		IJob iconfigIJob=new IJob(jobKey,configJob,cronExpression,dataMap);
		iconfigIJob.setStartTime(plan.getBeginDate());
		iconfigIJob.setEndTime(DateUtil.parseDate(plan.getEndDateStr()+" 23:59:59", DateUtil.DEFAULT_DATETIME_FORMAT));
		return iconfigIJob;
	}
	
	/**
	 * 批量删除job
	 * @throws ClassNotFoundException
	 * @throws SchedulerException
	 */
	public void stopJob(long[] planIds) throws ClassNotFoundException, SchedulerException{
		for(long planId : planIds){
			String jobKey=JOB_KEY+planId;
			this.scheduleManager.deleteJob(jobKey);
		}
	}

	/**
	 * 通过备份策略组装job的启动时间
	 * @return
	 */
	private String getCronExpression(BackupPlanBo plan){
		String cron="0 "+plan.getMinute()+" "+plan.getHour();
		if(plan.getType().equals(1)){//每日备份计划
			cron += " * * ?";
		}else if(plan.getType().equals(2)){//每周备份计划
			int cronWeek = Integer.parseInt(plan.getWeek());
			cron += " ? * "+cronWeek;
		}else if(plan.getType().equals(3)){//每月备份计划
			int cronDay = Integer.parseInt(plan.getDay());
			cron += " "+cronDay+" * ?";
		}else if(plan.getType().equals(4)){//每年备份计划
			int cronDay = Integer.parseInt(plan.getDay());
			int cronMonth = Integer.parseInt(plan.getMonth());
			cron += " "+cronDay+" "+cronMonth+" ?";
		}
		return cron;
	}
	public ScheduleManager getScheduleManager() {
		return scheduleManager;
	}
	public void setScheduleManager(ScheduleManager scheduleManager) {
		this.scheduleManager = scheduleManager;
	}
}
