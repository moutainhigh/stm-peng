package com.mainsteam.stm.topo.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.mainsteam.stm.topo.api.IIpMacAlarmTaskApi;
import com.mainsteam.stm.util.SpringBeanUtil;

/**
 * <li>网络设备IP-MAC-PORT告警任务Job</li>
 * @version  ms.stm
 * @since  2019年12月13日
 * @author zwx
 */
@DisallowConcurrentExecution
@Component
public class IpMacAlarmJob implements Job{
	private  final Log logger = LogFactory.getLog(IpMacAlarmJob.class);
	
	/**
	 * IP-MAC-PORT告警需要实现的业务
	 * @param ctx
	 * @throws JobExecutionException
	 */
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		try {
			//开始刷新ip-mac-port数据，并产生告警
			IIpMacAlarmTaskApi alarmTaskApi = SpringBeanUtil.getBean(IIpMacAlarmTaskApi.class);
			if(null != alarmTaskApi){
				alarmTaskApi.refreshIpMacPort(null);
			}
		} catch (Exception e) {
			logger.error("IpMacPort告警发生异常!",e);
		}
	}
}
