package com.mainsteam.stm.ct.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.mainsteam.stm.ct.api.IProbeTaskApi;
import com.mainsteam.stm.util.SpringBeanUtil;

@DisallowConcurrentExecution
@Component
public class ProbeJob implements Job{
	private  final Log logger = LogFactory.getLog(ProbeJob.class);
	
	/**
	 * IP-MAC-PORT告警需要实现的业务
	 * @param ctx
	 * @throws JobExecutionException
	 */
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		try {
			//开始刷新ip-mac-port数据，并产生告警
			IProbeTaskApi probeTask = SpringBeanUtil.getBean(IProbeTaskApi.class);
			if(null != probeTask){
				probeTask.checkStatus();
			}
		} catch (Exception e) {
			logger.error("IpMacPort告警发生异常!",e);
		}
	}
}
