package com.mainsteam.stm.instancelib.remote;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.service.ResourceInstanceExtendService;

public class ResourceInstanceSync implements ResourceInstanceSyncMBean{
	private static final Log logger = LogFactory
			.getLog(ResourceInstanceSync.class);
	
	private ResourceInstanceExtendService resourceInstanceExtendService;
	
	public void start(){
		System.out.println("ResourceInstanceSyncMBean  has start!");
	}
	
	@Override
	public List<ResourceInstance> dataSyncToCollector(long nodeGroupId) {
		if(logger.isDebugEnabled()){
			logger.debug("dataSyncToCollector start");
		}
		List<ResourceInstance> parentResourceInstances = null;
		try {
			parentResourceInstances = resourceInstanceExtendService.loadAllResourceInstance(nodeGroupId);
		} catch (Exception e) {
			logger.error("sync is faild",e);
		}
		if(logger.isDebugEnabled()){
			logger.debug("dataSyncToCollector end");
		}
		return parentResourceInstances;
	}

	public ResourceInstanceExtendService getResourceInstanceExtendService() {
		return resourceInstanceExtendService;
	}

	public void setResourceInstanceExtendService(
			ResourceInstanceExtendService resourceInstanceExtendService) {
		this.resourceInstanceExtendService = resourceInstanceExtendService;
	}
}
