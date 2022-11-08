/**
 * 
 */
package com.mainsteam.stm.pluginserver.manager;

import java.util.List;

import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginserver.obj.ReponseData;

/**
 * @author ziw
 * 
 */
public interface RequestReponseSynch {
	public PluginRequestMonitor createPluginRequestMonitor(
			List<PluginRequest> requests);

	public void recieveMetricData(long requestId, ReponseData result);
}
