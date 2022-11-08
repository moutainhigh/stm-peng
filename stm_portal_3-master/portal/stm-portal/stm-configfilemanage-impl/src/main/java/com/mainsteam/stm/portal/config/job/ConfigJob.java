package com.mainsteam.stm.portal.config.job;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import com.mainsteam.stm.portal.config.bo.BackupPlanBo;

/**
 * <li>文件名称: ConfigJob.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月29日
 * @author   caoyong
 */
@DisallowConcurrentExecution
public class ConfigJob implements Job {
	/**
	 * 日志
	 */
	private  final Log logger = LogFactory.getLog(ConfigJob.class);
	
	@Override
	public void execute(JobExecutionContext jobExec) throws JobExecutionException {
		JobKey key=jobExec.getTrigger().getJobKey();
		if(key.getName().equals("Configfilemanage-Job-1")){//特殊处理日备份
			long current=new Date().getTime();
			long next=jobExec.getNextFireTime().getTime()-86400*1000;
			String currentStr=Long.toString(current).substring(0, Long.toString(current).length()-3);
			String nextStr=Long.toString(next).substring(0, Long.toString(next).length()-3);
			long currentL=Long.parseLong(currentStr);
			long nextL=Long.parseLong(nextStr);
			if(currentL>=nextL){
				JobDataMap dataMap = jobExec.getJobDetail().getJobDataMap();
				BackupPlanBo plan = (BackupPlanBo) dataMap.get("plan");
				ConfigTask configTask= new ConfigTask();
				try {
					configTask.start(plan);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
				logger.info("Job success---------------------------------------");
			}else{
				logger.info("Job fail---------------------------------------");
			}
		}else{
			JobDataMap dataMap = jobExec.getJobDetail().getJobDataMap();
			BackupPlanBo plan = (BackupPlanBo) dataMap.get("plan");
			ConfigTask configTask= new ConfigTask();
			try {
				configTask.start(plan);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			logger.info("Job success---------------------------------------");
		}
	
		
	}

}
