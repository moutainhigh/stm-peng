/**
 * 
 */
package com.mainsteam.stm.scheduler.obj;

import com.mainsteam.stm.executor.obj.CustomMetricPluginParameter;

/**
 * @author ziw
 * 
 */
public class CustomMetricScheduleTask extends MetricScheduleTask {

	private CustomMetricPluginParameter pluginParameter;

	/**
	 * 
	 */
	public CustomMetricScheduleTask() {
	}

	/**
	 * @return the pluginParameter
	 */
	public final CustomMetricPluginParameter getPluginParameter() {
		return pluginParameter;
	}

	/**
	 * @param pluginParameter
	 *            the pluginParameter to set
	 */
	public final void setPluginParameter(
			CustomMetricPluginParameter pluginParameter) {
		this.pluginParameter = pluginParameter;
	}
}
