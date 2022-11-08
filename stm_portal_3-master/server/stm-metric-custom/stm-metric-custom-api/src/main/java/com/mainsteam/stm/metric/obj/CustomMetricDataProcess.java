package com.mainsteam.stm.metric.obj;

import java.io.Serializable;

import com.mainsteam.stm.metric.objenum.CustomMetricDataProcessWayEnum;

public class CustomMetricDataProcess implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6670287461557356170L;

	private String metricId;
	
	private String pluginId;
	
	private CustomMetricDataProcessWayEnum dataProcessWay = CustomMetricDataProcessWayEnum.NONE;

	public String getMetricId() {
		return metricId;
	}

	public String getPluginId() {
		return pluginId;
	}

	public CustomMetricDataProcessWayEnum getDataProcessWay() {
		return dataProcessWay;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	public void setDataProcessWay(CustomMetricDataProcessWayEnum dataProcessWay) {
		this.dataProcessWay = dataProcessWay;
	}
}
