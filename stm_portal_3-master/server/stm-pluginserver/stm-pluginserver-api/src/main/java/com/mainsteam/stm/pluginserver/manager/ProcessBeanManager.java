/**
 * 
 */
package com.mainsteam.stm.pluginserver.manager;

import com.mainsteam.stm.pluginprocessor.PluginResultSetConverter;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;

/**
 * @author ziw
 * 
 */
public interface ProcessBeanManager {
	public PluginResultSetProcessor getPluginResultSetProcessor(
			Class<? extends PluginResultSetProcessor> classObj)
			throws InstantiationException, IllegalAccessException;

	public PluginResultSetConverter getPluginResultSetConverter(
			Class<? extends PluginResultSetConverter> classObj)
			throws InstantiationException, IllegalAccessException;

}
