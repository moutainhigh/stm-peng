package com.mainsteam.stm.instancelib.interceptor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.remote.CollectorDiscoverPropSyncMBean;
import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.rpc.client.OCRPCClient;

/**
 * 添加资源实例拦截类（同步处理器资源到采集器）
 * @author xiaoruqiang
 */
public class DiscoverPropInterceptorUpdateImpl implements InstancelibInterceptor {

	private static final Log logger = LogFactory
			.getLog(DiscoverPropInterceptorUpdateImpl.class);
	
	private InstancelibEventManager	instancelibEventManager;
	
	private LocaleNodeService localNodeService;
	
	private OCRPCClient client;
	
	private ResourceInstanceService resourceInstanceService;
	
	public void start(){
		instancelibEventManager.register(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void interceptor(InstancelibEvent instancelibEvent) {
		if(instancelibEvent.getEventType() == EventEnum.DISCOVER_PROPERTY_UPDATE_EVENT){
			if(logger.isTraceEnabled()){
				logger.trace("update DiscoverProp interceptor start");
			}
			Object currentDiscoverProp = instancelibEvent.getCurrent();
			List<DiscoverProp> discoverProps = null;
			if(currentDiscoverProp instanceof List){
				discoverProps = (List<DiscoverProp>)currentDiscoverProp;
			}else{
				discoverProps = new ArrayList<>();
				DiscoverProp discoverProp = (DiscoverProp)instancelibEvent.getCurrent();
				discoverProps.add(discoverProp);
			}
			
			HashSet<Long> instanceIds = new HashSet<>();
			/*
			 * 遍历InstanceId 如果不一样,有可能需要同步到多个多个采集器
			 */
			for (DiscoverProp item : discoverProps) {
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
						logger.error("update DiscoverProp  remote get resourceinstance error!", e);
					}
				}
			}
			for (String nodeGroupId : nodeGroupIds) {
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
						logger.warn("update DiscoverProp  remote get node is null");
						return;
					}
			    }else{
			    	CollectorDiscoverPropSyncMBean collectorDiscoverPropSyncMBean= null;
					try {
						collectorDiscoverPropSyncMBean  = client.getRemoteSerivce(node,CollectorDiscoverPropSyncMBean.class);
						if(collectorDiscoverPropSyncMBean == null){
							if(logger.isWarnEnabled()){
								logger.warn("update DiscoverProp remote collector MBen not found!");
							}
						}else{
							collectorDiscoverPropSyncMBean.updateDiscoverPropSyncToCollector(discoverProps);
						}
					} catch (Exception e) {
						if(logger.isErrorEnabled()){
							logger.error("update DiscoverProp sync to collector error!", e);
						}
					}
			    }
			}
			if(logger.isTraceEnabled()){
				logger.trace("update DiscoverProp interceptor end");
			}
		}
	}
	
	public void setInstancelibEventManager(
			InstancelibEventManager instancelibEventManager) {
		this.instancelibEventManager = instancelibEventManager;
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
