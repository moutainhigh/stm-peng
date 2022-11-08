package com.mainsteam.stm.portal.threed.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.job.IJob;
import com.mainsteam.stm.job.ScheduleManager;
import com.mainsteam.stm.portal.threed.util.MetricUtil;
import com.mainsteam.stm.portal.threed.util.jaxb.Metrics;

public class MoniterEngine {
	/**
	 * 日志
	 */
	private  final Log logger = LogFactory.getLog(MoniterEngine.class);
	/**
	 * job计划Manager
	 */
	private ScheduleManager scheduleManager;
	/**
	 * job key
	 */
	private static final String JOB_KEY = "3D-Moniter-Job";
	
	/**
	 * 启动job任务
	 * @param
	 */
	public void startJob() throws ClassNotFoundException, SchedulerException, InstancelibException{
		//五分钟推送一次
		String cron = "0 */5 * * * ?";
		try {
			Metrics metrics = MetricUtil.getMetrics();
			//从配置文件中取
			cron = metrics.getCron();
		} catch (Exception e) {
			logger.error("从metric.xml中获取cron异常,使用默认五分钟一次cron", e);
		}
		IJob job = new IJob(JOB_KEY, new MoniterJob(),cron);
		//调度Job
		this.scheduleManager.scheduleJob(job);
	}
	public void stopJob() throws ClassNotFoundException, SchedulerException{
		this.scheduleManager.deleteJob(JOB_KEY);
	}
	
	
	public void setScheduleManager(ScheduleManager scheduleManager) {
		this.scheduleManager = scheduleManager;
	}

}
