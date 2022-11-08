/**
 * 
 */
package com.mainsteam.stm.pluginserver.pool;

import java.util.HashMap;
import java.util.Map;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

/**
 * @author ziw
 * 
 */
public class PluginInitParameterData implements PluginInitParameter {

	private Map<String, Parameter> parameterMap;

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
		}else {
			return null;
		}
	}

	public void setParameter(String key, Parameter value) {
		if(value == null){
			return;
		}
		parameterMap.put(key, value);
	}
}
