package com.mainsteam.stm.metric.dao.pojo;

public class CustomMetricCollectDO {

	private long metricCollectId;
	
	private String metricId;
	
	private String pluginId;
	
	private String paramKey;
	
	private String paramType;
	
	private String paramValue;
	
	//private int executeOrder;
	
	private String dataProcessWay;
	
	public String getMetricId() {
		return metricId;
	}

	public String getPluginId() {
		return pluginId;
	}

	public String getParamKey() {
		return paramKey;
	}

	public String getParamType() {
		return paramType;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public long getMetricCollectId() {
		return metricCollectId;
	}

	public void setMetricCollectId(long metricCollectId) {
		this.metricCollectId = metricCollectId;
	}

//	public int getExecuteOrder() {
//		return executeOrder;
//	}
//
//	public void setExecuteOrder(int executeOrder) {
//		this.executeOrder = executeOrder;
//	}

	public String getDataProcessWay() {
		return dataProcessWay;
	}

	public void setDataProcessWay(String dataProcessWay) {
		this.dataProcessWay = dataProcessWay;
	}

}
