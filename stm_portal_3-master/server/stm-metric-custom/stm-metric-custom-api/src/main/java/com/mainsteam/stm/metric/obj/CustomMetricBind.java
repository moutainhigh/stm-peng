/**
 * 
 */
package com.mainsteam.stm.metric.obj;

/**
 * 自定义指标绑定关系
 * 
 * @author ziw
 *
 */
public class CustomMetricBind {

	private String metricId;
	
	private long instanceId;
	
	private String pluginId;
	
	/**
	 * 
	 */
	public CustomMetricBind() {
	}

	/**
	 * @return the metricId
	 */
	public final String getMetricId() {
		return metricId;
	}

	/**
	 * @param metricId the metricId to set
	 */
	public final void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	/**
	 * @return the instanceId
	 */
	public final long getInstanceId() {
		return instanceId;
	}

	/**
	 * @param instanceId the instanceId to set
	 */
	public final void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	/**
	 * @return the pluginId
	 */
	public final String getPluginId() {
		return pluginId;
	}

	/**
	 * @param pluginId the pluginId to set
	 */
	public final void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}
}
