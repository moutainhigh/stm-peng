/**
 * 
 */
package com.mainsteam.stm.executor.obj;

/**
 * @author ziw
 *
 */
public class CustomMetricExecuteParameter extends MetricExecuteParameter {

	private CustomMetricPluginParameter pluginParameter;
	/**
	 * 
	 */
	public CustomMetricExecuteParameter() {
	}
	
	
	/**
	 * @param pluginParameter
	 */
	public CustomMetricExecuteParameter(
			CustomMetricPluginParameter pluginParameter) {
		this.pluginParameter = pluginParameter;
	}




	/**
	 * @return the pluginParameter
	 */
	public final CustomMetricPluginParameter getPluginParameter() {
		return pluginParameter;
	}
	/**
	 * @param pluginParameter the pluginParameter to set
	 */
	public final void setPluginParameter(CustomMetricPluginParameter pluginParameter) {
		this.pluginParameter = pluginParameter;
	}
}
