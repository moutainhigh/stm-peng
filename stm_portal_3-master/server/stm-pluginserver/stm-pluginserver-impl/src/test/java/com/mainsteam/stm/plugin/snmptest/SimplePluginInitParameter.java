/**
 * 
 */
package com.mainsteam.stm.plugin.snmptest;

import java.util.ArrayList;
import java.util.List;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

/**
 * @author fevergreen
 * 
 */
public class SimplePluginInitParameter implements PluginInitParameter {

	private List<Parameter> parameters;

	/**
	 * 
	 */
	public SimplePluginInitParameter() {
		this.parameters = new ArrayList<>();
	}

	@Override
	public String getParameterValueByKey(String key) {
		for (Parameter p : parameters) {
			if (p.getKey().equals(key)) {
				return p.getValue();
			}
		}
		return null;
	}

	public void addParameterValueByKey(String key, String value) {
		ParameterValue p = new ParameterValue();
		p.setKey(key);
		p.setValue(value);
		this.parameters.add(p);
	}

	@Override
	public Parameter[] getParameters() {
		return this.parameters.toArray(new Parameter[this.parameters.size()]);
	}

	public void setParameters(Parameter[] parameters) {
		if (parameters != null && parameters.length > 0) {
			this.parameters = new ArrayList<>(parameters.length);
			for (int i = 0; i < parameters.length; i++) {
				this.parameters.add(parameters[i]);
			}
		} else {
			this.parameters = new ArrayList<>();
		}
	}
}
