package com.mainsteam.stm.instancelib.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.remote.CollectorResourceInstanceSync;
import com.mainsteam.stm.instancelib.remote.CollectorResourceInstanceSyncMBean;
import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.rpc.client.OCRPCClient;

/**
 * 添加资源实例拦截类（同步处理器资源到采集器）
 * @author xiaoruqiang
 */
public class InstancelibInterceptorAddImpl implements InstancelibInterceptor {

	private static final Log logger = LogFactory
			.getLog(CollectorResourceInstanceSync.class);
	
	private InstancelibEventManager	instancelibEventManager;
	
	private LocaleNodeService localNodeService;
	
	private OCRPCClient client;
	
	public void start(){
		instancelibEventManager.register(this);
	}
	
	@Override
	public void interceptor(InstancelibEvent instancelibEvent) {
		if(instancelibEvent.getEventType() == EventEnum.INSTANCE_ADD_EVENT){
			ResourceInstance resourceInstance = (ResourceInstance)instancelibEvent.getSource();
			if(logger.isTraceEnabled()){
				logger.trace("add resource instance interceptor start instanceId=" + resourceInstance.getId() + " instanceName="+resourceInstance.getName());
			}
			Node node = null;
			try {
			    node  = localNodeService.getLocalNodeTable().getNodeInGroup(Integer.parseInt(resourceInstance.getDiscoverNode()));
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("discoveryResourceInstance", e);
				}
			}
			if(node == null){
				if (logger.isWarnEnabled()) {
					logger.warn("add resource intance get node is null");
					return;
				}
		    }else{
		    	CollectorResourceInstanceSyncMBean collectorResourceInstanceSyncMBean= null;
				try {
				    collectorResourceInstanceSyncMBean  = client.getRemoteSerivce(node,CollectorResourceInstanceSyncMBean.class);
					if(collectorResourceInstanceSyncMBean == null){
						if(logger.isWarnEnabled()){
							logger.warn("add resource remote collector MBen not found!");
						}
					}else{
						collectorResourceInstanceSyncMBean.addResourceInstanceSyncToCollector(resourceInstance);
					}
				} catch (Exception e) {
					if(logger.isErrorEnabled()){
						logger.error("add resourceinstance sync to collector error!", e);
					}
				}
		    }
			if(logger.isTraceEnabled()){
				logger.trace("add resource instance interceptor end instanceId=" + resourceInstance.getId() + " instanceName="+resourceInstance.getName());
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

	
}
