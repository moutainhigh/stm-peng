package com.mainsteam.stm.instancelib.remote;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.obj.ModuleProp;

public class CollectorModulePropSync implements CollectorModulePropSyncMBean {

	private static final Log logger = LogFactory.getLog(CollectorModulePropSync.class);

	private ModulePropService modulePropService;
	
	public void setModulePropService(ModulePropService modulePropService) {
		this.modulePropService = modulePropService;
	}

	public void start() {
		System.out.println("collector moduleprop Mben has start!");
	}
	
	@Override
	public void updateModulePropSyncToCollector(List<ModuleProp> moduleProps)
			throws Exception {
		if(logger.isTraceEnabled()){
			logger.trace("updateModulePropSyncToCollector start");
		}
		modulePropService.updateProps(moduleProps);
		if(logger.isTraceEnabled()){
			logger.trace("updateModulePropSyncToCollector end");
		}
	}

	
	

}
