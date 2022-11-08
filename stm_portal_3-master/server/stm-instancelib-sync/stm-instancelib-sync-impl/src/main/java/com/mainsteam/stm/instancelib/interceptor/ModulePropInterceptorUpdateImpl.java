package com.mainsteam.stm.instancelib.interceptor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.remote.CollectorModulePropSyncMBean;
import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.rpc.client.OCRPCClient;

/**
 * 添加资源实例拦截类（同步处理器资源到采集器）
 * @author xiaoruqiang
 */
public class ModulePropInterceptorUpdateImpl implements InstancelibInterceptor {

	private static final Log logger = LogFactory.getLog(ModulePropInterceptorUpdateImpl.class);
	
	private LocaleNodeService localNodeService;
	
	private OCRPCClient client;
	
	private ResourceInstanceService resourceInstanceService;
	
	@SuppressWarnings("unchecked")
	@Override
	public void interceptor(InstancelibEvent instancelibEvent) {
		if(instancelibEvent.getEventType() == EventEnum.MODULE_PROPERTY_UPDATE_EVENT){
			if(logger.isTraceEnabled()){
				logger.trace("update moduleprop interceptor start");
			}
			
			Object currentModuleProp = instancelibEvent.getCurrent();
			List<ModuleProp> moduleProps = null;
			if(currentModuleProp instanceof List){
				moduleProps = (List<ModuleProp>)currentModuleProp;
			}else{
				moduleProps = new ArrayList<>();
				ModuleProp moduleProp = (ModuleProp)instancelibEvent.getCurrent();
				moduleProps.add(moduleProp);
			}
			
			HashSet<Long> instanceIds = new HashSet<>();
			/*
			 * 遍历InstanceId 如果不一样,有可能需要同步到多个多个采集器
			 */
			for (ModuleProp item : moduleProps) {
				instanceIds.add(item.getInstanceId());
			}
			HashSet<String> nodeGroupIds = new HashSet<>();
			for (long instanceId : instanceIds) {
				try {
					ResourceInstance resourceInstance = resourceInstanceService.getResourceInstance(instanceId);
					if(resourceInstance != null){
						nodeGroupIds.add(resourceInstance.getDiscoverNode());
					}
				} catch (InstancelibException e) {
					if(logger.isErrorEnabled()){
						logger.error("update moduleprop  remote get resourceinstance error!", e);
					}
				}
			}
			for (String nodeGroupId : nodeGroupIds) {
				if(StringUtils.isEmpty(nodeGroupId)){
					continue;
				}
				Node node = null;
				try {
				    node  = localNodeService.getLocalNodeTable().getNodeInGroup(Integer.parseInt(nodeGroupId));
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("discoveryResourceInstance", e);
					}
				}
				if(node == null){
					if (logger.isWarnEnabled()) {
						logger.warn("update moduleprop  remote get node is null");
						return;
					}
			    }else{
			    	CollectorModulePropSyncMBean collectorModulePropSyncMBean= null;
					try {
						collectorModulePropSyncMBean  = client.getRemoteSerivce(node,CollectorModulePropSyncMBean.class);
						if(collectorModulePropSyncMBean == null){
							if(logger.isWarnEnabled()){
								logger.warn("update moduleprop remote collector MBen not found!");
							}
						}else{
							collectorModulePropSyncMBean.updateModulePropSyncToCollector(moduleProps);
						}
					} catch (Exception e) {
						if(logger.isErrorEnabled()){
							logger.error("update moduleprop sync to collector error!", e);
						}
					}
			    }
			}
			if(logger.isTraceEnabled()){
				logger.trace("update moduleprop interceptor end");
			}
		}
	}
	
	public void setClient(OCRPCClient client) {
		this.client = client;
	}
	
	public void setLocalNodeService(LocaleNodeService localNodeService) {
		this.localNodeService = localNodeService;
	}

	public void setResourceInstanceService(
			ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}
	
}
