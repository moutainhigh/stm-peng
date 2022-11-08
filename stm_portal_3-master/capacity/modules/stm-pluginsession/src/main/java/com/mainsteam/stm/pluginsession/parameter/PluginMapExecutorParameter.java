/**
 * 
 */
package com.mainsteam.stm.pluginsession.parameter;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author fevergreen
 * 
 */
public class PluginMapExecutorParameter implements
		PluginExecutorParameter<Map<String, Parameter>> {

	public PluginMapExecutorParameter(){
		super();
	}
	
	private Map<String, Parameter> parameter;

	public Map<String, Parameter> getParameters() {
		return parameter;
	}

	public void setParameters(Map<String, Parameter> parameter) {
		this.parameter = new TreeMap<>(parameter);
	}

	public void addParameter(String key, Parameter p) {
		if (this.parameter == null) {
			this.parameter = new TreeMap<String, Parameter>();
		}
		this.parameter.put(key, p);
	}

	public Parameter getParameter(String key) {
		if (this.parameter != null) {
			return this.parameter.get(key);
		}
		return null;
	}
}
