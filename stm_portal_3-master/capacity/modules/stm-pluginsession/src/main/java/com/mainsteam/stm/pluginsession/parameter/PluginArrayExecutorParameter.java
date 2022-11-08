/**
 * 
 */
package com.mainsteam.stm.pluginsession.parameter;

/**
 * @author fevergreen
 *
 */
public class PluginArrayExecutorParameter implements PluginExecutorParameter<Parameter[]> {
	
	private Parameter[] parameters;
	
	@Override
	public Parameter[] getParameters() {
		return parameters;
	}

	public void setParameters(Parameter[] parameters) {
		this.parameters = parameters;
	}
	
	public String[] getParametersValue(){
		if(parameters == null){
			return null;
		}
		String[] values = new String[parameters.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = parameters[i].getValue();
		}
		return values;
	}
}
