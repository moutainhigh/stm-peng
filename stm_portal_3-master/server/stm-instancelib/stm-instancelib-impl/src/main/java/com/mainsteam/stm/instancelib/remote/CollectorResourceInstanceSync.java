package com.mainsteam.stm.instancelib.remote;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;

public class CollectorResourceInstanceSync implements
		CollectorResourceInstanceSyncMBean {
	private static final Log logger = LogFactory
			.getLog(CollectorResourceInstanceSync.class);

	private ResourceInstanceService resourceInstanceService;

	public void setResourceInstanceService(
			ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}

	public void start() {
		System.out.println("collector resourceinstance Mben has start!");
	}

	@Override
	public void addResourceInstanceSyncToCollector(
			ResourceInstance resourceInstance) throws InstancelibException {
		if(logger.isTraceEnabled()){
			logger.trace("addResourceInstanceSyncToCollector start");
		}
		resourceInstanceService.addResourceInstance(resourceInstance);
		if(logger.isTraceEnabled()){
			logger.trace("addResourceInstanceSyncToCollector end");
		}
	}
	
	@Override
	public void refreshResourceInstanceSyncToCollector(
			ResourceInstance resourceInstance) throws InstancelibException {
		if(logger.isTraceEnabled()){
			logger.trace("refreshResourceInstanceSyncToCollector start");
		}
		resourceInstanceService.refreshResourceInstance(resourceInstance);
		if(logger.isTraceEnabled()){
			logger.trace("refreshResourceInstanceSyncToCollector end");
		}
	}

	@Override
	public void refreshResourceInstancesSyncToCollector(
			List<ResourceInstance> resourceInstances)
			throws InstancelibException {
		if(logger.isTraceEnabled()){
			logger.trace("refreshResourceInstanceSyncToCollector start");
		}
		for (ResourceInstance resourceInstance : resourceInstances) {
			resourceInstanceService.refreshResourceInstance(resourceInstance);
		}
		if(logger.isTraceEnabled()){
			logger.trace("refreshResourceInstanceSyncToCollector end");
		}
	}

	@Override
	public void addResourceInstancesSyncToCollector(
			List<ResourceInstance> resourceInstances)
			throws InstancelibException {
		if(logger.isTraceEnabled()){
			logger.trace("addResourceInstancesSyncToCollector start");
		}
		for (ResourceInstance resourceInstance : resourceInstances) {
			resourceInstanceService.addResourceInstance(resourceInstance);
		}
		if(logger.isTraceEnabled()){
			logger.trace("addResourceInstancesSyncToCollector end");
		}
	}
}
