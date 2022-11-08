/**
 * 
 */
package com.mainsteam.stm.pluginserver;

import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginserver.excepton.PluginServerExecuteException;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginserver.message.PluginResult;
import com.mainsteam.stm.pluginsession.PluginSessionContext;

/**
 * @author ziw
 * 
 */
public interface PluginProcesser {
	public PluginResult process(PluginRequest request,
			PluginSessionContext context, ResultSet set)
			throws PluginServerExecuteException ;
}
