/**
 * 
 */
package com.mainsteam.stm.metric.obj;

import java.io.Serializable;


/**
 * @author ziw
 *
 */
public class CustomMetricCollectParameter implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -163001263476828354L;

	private String metricId;
	
	private String pluginId;
	
	private String parameterKey;
	
	private String parameterValue;
	
	private String parameterType;

	//private CustomMetricDataProcessWayEnum dataProcessWay = CustomMetricDataProcessWayEnum.NONE;
	
	/**
	 * 
	 */
	public CustomMetricCollectParameter() {
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

	/**
	 * @return the parameterKey
	 */
	public final String getParameterKey() {
		return parameterKey;
	}

	/**
	 * @param parameterKey the parameterKey to set
	 */
	public final void setParameterKey(String parameterKey) {
		this.parameterKey = parameterKey;
	}

	/**
	 * @return the parameterValue
	 */
	public final String getParameterValue() {
		return parameterValue;
	}

	/**
	 * @param parameterValue the parameterValue to set
	 */
	public final void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}

	/**
	 * @return the parameterType
	 */
	public final String getParameterType() {
		return parameterType;
	}

	/**
	 * @param parameterType the parameterType to set
	 */
	public final void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

//	public CustomMetricDataProcessWayEnum getDataProcessWay() {
//		return dataProcessWay;
//	}
//
//	public void setDataProcessWay(CustomMetricDataProcessWayEnum dataProcessWay) {
//		this.dataProcessWay = dataProcessWay;
//	}
}
