package com.mainsteam.stm.instancelib.service.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.instancelib.CoreResourceInstanceService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.dao.ResourceInstanceDAO;
import com.mainsteam.stm.instancelib.dao.pojo.ResourceInstanceDO;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;

public class CoreResourceInstanceServiceImpl implements
		CoreResourceInstanceService {
	private static final Log logger = LogFactory.getLog(CoreResourceInstanceServiceImpl.class);
	
	private ResourceInstanceService resourceInstanceService;
	
	private ResourceInstanceDAO resourceInstanceDAO;
	
	@Override
	public void setCoreResourceInstance(String ip) {
		if(logger.isTraceEnabled()){
			logger.trace("setCoreResourceInstance start ip=" + ip);
		}
		long coreInstanceId = getCoreResourceInstance();
		try {
			long instanceId = 0;
			List<ResourceInstance> parentInstance = resourceInstanceService.getAllParentInstance();
			start:for (ResourceInstance resourceInstance : parentInstance) {
				String[] deviceIps = resourceInstance.getModulePropBykey(MetricIdConsts.METRIC_IP);
				for (String deviceIp : deviceIps) {
					if(ip.equals(deviceIp)){
						instanceId = resourceInstance.getId();
						break start;
					}
				}
			}
			if(instanceId != 0){
				ResourceInstanceDO resourceInstanceDO = new ResourceInstanceDO();
				resourceInstanceDO.setIsCore("1");
				resourceInstanceDO.setInstanceId(instanceId);
				resourceInstanceDAO.updateResourceInstance(resourceInstanceDO);
			}
			if(coreInstanceId > 0){
				ResourceInstanceDO resourceInstanceDO = new ResourceInstanceDO();
				resourceInstanceDO.setIsCore("0");
				resourceInstanceDO.setInstanceId(instanceId);
				resourceInstanceDAO.updateResourceInstance(resourceInstanceDO);
			}
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("setCoreResourceInstance getAllParentInstance error!",e);
			}
		}
		if(logger.isTraceEnabled()){
			logger.trace("setCoreResourceInstance end ip=" + ip);
		}
	}

	@Override
	public void setCoreResourceInstance(long instanceId) {
		if(logger.isTraceEnabled()){
			logger.trace("setCoreResourceInstance start instanceId=" + instanceId);
		}
		long coreInstanceId = getCoreResourceInstance();
		try {
			if(instanceId > 0){
				ResourceInstanceDO resourceInstanceDO = new ResourceInstanceDO();
				resourceInstanceDO.setIsCore("1");
				resourceInstanceDO.setInstanceId(instanceId);
				resourceInstanceDAO.updateResourceInstance(resourceInstanceDO);
			}
			if(coreInstanceId > 0){
				ResourceInstanceDO resourceInstanceDO = new ResourceInstanceDO();
				resourceInstanceDO.setIsCore("0");
				resourceInstanceDO.setInstanceId(instanceId);
				resourceInstanceDAO.updateResourceInstance(resourceInstanceDO);
			}
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("setCoreResourceInstance getAllParentInstance error!",e);
			}
		}
		if(logger.isTraceEnabled()){
			logger.trace("setCoreResourceInstance end instanceId=" + instanceId);
		}
	}
	
	@Override
	public long getCoreResourceInstance() {
		if(logger.isTraceEnabled()){
			logger.trace("getCoreResourceInstance start");
		}
		long coreInstanceId = 0;
		ResourceInstanceDO resourceInstanceDO = new ResourceInstanceDO();
		resourceInstanceDO.setIsCore("1");
		try {
			List<ResourceInstanceDO> resourceInstanceDOs = resourceInstanceDAO.getInstancesByResourceDO(resourceInstanceDO);
			if(resourceInstanceDOs != null && !resourceInstanceDOs.isEmpty()){
				coreInstanceId = resourceInstanceDOs.get(0).getInstanceId();
			}
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("getCoreResourceInstance",e);
			}
		}
		if(logger.isTraceEnabled()){
			logger.trace("getCoreResourceInstance end");
		}
		return coreInstanceId;
	}

	public void setResourceInstanceService(
			ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}
	
	public void setResourceInstanceDAO(ResourceInstanceDAO resourceInstanceDAO) {
		this.resourceInstanceDAO = resourceInstanceDAO;
	}

	

}

