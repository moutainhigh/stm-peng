/**
 * 
 */
package com.mainsteam.stm.pluginserver.adapter.manager.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.pluginserver.adapter.manager.PluginRequestMonitor;
import com.mainsteam.stm.pluginserver.adapter.manager.RequestReponseSynch;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginserver.obj.ReponseData;

/**
 * @author ziw
 * 
 */
public class RequestReponseSynchImpl implements RequestReponseSynch {
	
	private static final Log logger = LogFactory.getLog(RequestReponseSynchImpl.class);

	private List<PluginRequestMonitor> pluginRequestMonitors;

	/**
	 * 
	 */
	public RequestReponseSynchImpl() {
	}

	public void start() {
		pluginRequestMonitors = new ArrayList<>();
	}

	public synchronized PluginRequestMonitor createPluginRequestMonitor(
			List<PluginRequest> requests) {
		PluginRequestMonitor monitor = new PluginRequestMonitorImpl(requests);
		pluginRequestMonitors.add(monitor);
		return monitor;
	}

	public synchronized void recieveMetricData(Long requestId,
			ReponseData result) {
		if (logger.isDebugEnabled()) {
			logger.debug("recieveMetricData requestId="+requestId);
		}
		if (pluginRequestMonitors.size() > 0) {
			for (Iterator<PluginRequestMonitor> iterator = pluginRequestMonitors
					.iterator(); iterator.hasNext();) {
				PluginRequestMonitor m = iterator.next();
				m.recieveResult(result, requestId);
				if (m.isFinished()) {
					iterator.remove();
				}
			}
		}
	}
}
