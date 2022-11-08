package com.mainsteam.stm.metric.dao.pojo;

import java.io.Serializable;

public class CustomMetricBindDO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2197108921134332516L;

	private long instanceId;

	private String metricId;

	private String pluginId;

	public long getInstanceId() {
		return instanceId;
	}

	public String getMetricId() {
		return metricId;
	}

	public String getPluginId() {
		return pluginId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

}
