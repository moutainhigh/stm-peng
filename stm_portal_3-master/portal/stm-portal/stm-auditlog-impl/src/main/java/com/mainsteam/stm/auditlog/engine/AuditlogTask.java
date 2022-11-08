package com.mainsteam.stm.auditlog.engine;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.auditlog.api.IAuditlogApi;
import com.mainsteam.stm.util.SpringBeanUtil;


public class AuditlogTask{
	
	private  final Log  logger = LogFactory.getLog(AuditlogTask.class);
	
	public void start() throws Exception {
		IAuditlogApi stm_system_AuditlogApi = (IAuditlogApi)SpringBeanUtil.getObject("stm_system_AuditlogApi");
		if(logger.isDebugEnabled()){
			logger.debug("AuditlogTask do start");
		}
		logger.debug("AuditlogTask.start():"+new Date());
		try{
			stm_system_AuditlogApi.insertBuAuditlog();
		}catch(Exception e){
			e.printStackTrace();
			logger.debug("printStackTrace:"+e);
		}
	}
}
