/**
 * 
 */
package com.mainsteam.stm.pluginserver.context;

import java.util.HashMap;
import java.util.Map;

import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginsession.PluginSessionContext;

/**
 * @author ziw
 * 
 */
public class PluginSessionContextManager {

	private volatile Map<String, PluginSessionContext> contextsMap;

	/**
	 * 
	 */
	public PluginSessionContextManager() {
		contextsMap = new HashMap<String, PluginSessionContext>();
	}

	public PluginSessionContext getContext(PluginRequest request) {
		PluginSessionContextImpl c = null;
		if (contextsMap.containsKey(request.getPluginSessionKey())) {
			c = (PluginSessionContextImpl) contextsMap.get(request
					.getPluginSessionKey());
		} else {
			/**
			 * 保证线程安全
			 * 
			 * modify by ziw at 2017年3月21日 下午8:42:55
			 */
			synchronized (this) {
				if (contextsMap.containsKey(request.getPluginSessionKey())) {
					c = (PluginSessionContextImpl) contextsMap.get(request
							.getPluginSessionKey());
				} else {
					c = new PluginSessionContextImpl();
					contextsMap.put(request.getPluginSessionKey(), c);
				}
			}
		}
		c.setMetricCollectTime(request.getCollectTime());
		c.setMetricId(request.getMetricId());
		c.setResourceId(request.getResourceId());
		c.setResourceInstanceId(request.getResourceInstId());
		return c;
	}

	public synchronized void removeSessionContext(PluginRequest request) {
		contextsMap.remove(request.getPluginSessionKey());
	}

	public synchronized void clear() {
		contextsMap.clear();
	}
}
