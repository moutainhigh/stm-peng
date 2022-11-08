package com.mainsteam.stm.instancelib.interceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
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
			.getLog(InstancelibInterceptorAddImpl.class);
	
	
	private LocaleNodeService localNodeService;
	
	private OCRPCClient client;
	
	@Override
	public void interceptor(InstancelibEvent instancelibEvent) {
		if(instancelibEvent.getEventType() == EventEnum.INSTANCE_ADD_EVENT){
			List<ResourceInstance> resourceInstances = (List<ResourceInstance>)instancelibEvent.getSource();
			if(logger.isTraceEnabled()){
				logger.trace("add resource instance interceptor start =" + resourceInstances);
			}
			if(resourceInstances == null){
				return;
			}
			HashMap<String, List<ResourceInstance>> allInstace = new HashMap<>();
			for (ResourceInstance tempInstance : resourceInstances) {
				if(StringUtils.isEmpty(tempInstance.getDiscoverNode())){
					continue;
				}
				List<ResourceInstance> result = allInstace.get(tempInstance.getDiscoverNode());
				if(result == null){
					result = new ArrayList<ResourceInstance>();
					allInstace.put(tempInstance.getDiscoverNode(), result);
				}
				result.add(tempInstance);
			}
			for (Entry<String,List<ResourceInstance>> temp : allInstace.entrySet()) {
				Node node = null;
				try {
				    node  = localNodeService.getLocalNodeTable().getNodeInGroup(Integer.parseInt(temp.getKey()));
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("discoveryResourceInstance", e);
					}
				}
				if(node == null){
					if (logger.isWarnEnabled()) {
						logger.warn("refresh resource intance get node is null");
						continue;
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
							collectorResourceInstanceSyncMBean.addResourceInstancesSyncToCollector(temp.getValue());
						}
					} catch (Exception e) {
						if(logger.isErrorEnabled()){
							logger.error("add resourceinstance sync to collector error!", e);
						}
					}
			    }
			}
			if(logger.isTraceEnabled()){
				logger.trace("add resource instance interceptor end =" + resourceInstances);
			}
		}
	}
	
	public void setClient(OCRPCClient client) {
		this.client = client;
	}
	
	public void setLocalNodeService(LocaleNodeService localNodeService) {
		this.localNodeService = localNodeService;
	}

	
}
