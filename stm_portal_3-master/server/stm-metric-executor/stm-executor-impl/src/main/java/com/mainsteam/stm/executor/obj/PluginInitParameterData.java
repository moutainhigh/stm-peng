/**
 * 
 */
package com.mainsteam.stm.executor.obj;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

/**
 * @author ziw
 * 
 */
public class PluginInitParameterData implements PluginInitParameter {

	Map<String, Parameter> parameterMap;

	/**
	 * 
	 */
	public PluginInitParameterData() {
		parameterMap = new HashMap<String, Parameter>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.pluginsession.parameter.PluginInitParameter#getParameters
	 * ()
	 */
	@Override
	public Parameter[] getParameters() {
		return parameterMap.values()
				.toArray(new Parameter[parameterMap.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.pluginsession.parameter.PluginInitParameter#
	 * getParameterValueByKey(java.lang.String)
	 */
	@Override
	public String getParameterValueByKey(String key) {
		if (parameterMap.containsKey(key)) {
			return parameterMap.get(key).getValue();
		}
		return null;
	}

	public void setParameterMap(Map<String, Parameter> parameterMap) {
		this.parameterMap = parameterMap;
	}

	public void setParameter(String key, String value) {
		if (parameterMap.containsKey(key)) {
			return;
		}
		ParameterValue p = new ParameterValue();
		p.setKey(key);
		p.setValue(value);
		parameterMap.put(key, p);
	}

	public void setParameter(Map<String, String> parameterMap) {
		for (Iterator<String> iterator = parameterMap.keySet().iterator(); iterator
				.hasNext();) {
			String key = iterator.next();
			String value = parameterMap.get(key);
			setParameter(key, value);
		}
	}
}
