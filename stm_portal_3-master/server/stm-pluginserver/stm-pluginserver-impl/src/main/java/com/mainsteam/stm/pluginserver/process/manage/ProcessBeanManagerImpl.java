/**
 * 
 */
package com.mainsteam.stm.pluginserver.process.manage;

import java.util.HashMap;
import java.util.Map;

import com.mainsteam.stm.pluginprocessor.PluginResultSetConverter;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginserver.manager.ProcessBeanManager;

/**
 * @author ziw
 * 
 */
public class ProcessBeanManagerImpl implements ProcessBeanManager {

	private Map<Class<?>, Object> processerObjMap;

	/**
	 * 
	 */
	public ProcessBeanManagerImpl() {
		processerObjMap = new HashMap<>();
	}

	public PluginResultSetProcessor getPluginResultSetProcessor(
			Class<? extends PluginResultSetProcessor> classObj)
			throws InstantiationException, IllegalAccessException {
		return (PluginResultSetProcessor) getBean(classObj);
	}

	public PluginResultSetConverter getPluginResultSetConverter(
			Class<? extends PluginResultSetConverter> classObj)
			throws InstantiationException, IllegalAccessException {
		return (PluginResultSetConverter) getBean(classObj);
	}

	private Object getBean(Class<?> classObj) throws InstantiationException,
			IllegalAccessException {
		if (processerObjMap.containsKey(classObj)) {
			return processerObjMap.get(classObj);
		} else {
			Object p = classObj.newInstance();
			processerObjMap.put(classObj, p);
			return p;
		}
	}
}
