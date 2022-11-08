/**
 * 
 */
package com.mainsteam.stm.common.metric.obj;

import java.io.Serializable;

import com.mainsteam.stm.pluginprocessor.ParameterValue;

/**
 * @author ziw
 *
 */
public class CustomMetricMonitorInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6443521239951733917L;
	
	/**
	 *  key:资源实例id,value:profileId
	 */
	private long instanceId;

	/**
	 * 自定义指标id
	 */
	private String customMetricId;
	
	/**
	 * 自定义指标的数据处理方式
	 */
	private String customMetricProcessWay;
	
	/**
	 * 是否监控
	 */
	private boolean monitor;
	
	/**
	 * 监控频度
	 */
	private String freq;
	
	/**
	 * 插件id
	 */
	private String pluginId;

	/**
	 * 插件执行参数列表
	 */
	private ParameterValue[] parameters;
	
	private String changeAction;
	
	/**
	 * 
	 */
	public CustomMetricMonitorInfo() {
	}
	
	/**
	 * @return the customMetricProcessWay
	 */
	public final String getCustomMetricProcessWay() {
		return customMetricProcessWay;
	}


	/**
	 * @return the changeAction
	 */
	public final String getChangeAction() {
		return changeAction;
	}

	/**
	 * @param changeAction the changeAction to set
	 */
	public final void setChangeAction(String changeAction) {
		this.changeAction = changeAction;
	}

	/**
	 * @param customMetricProcessWay the customMetricProcessWay to set
	 */
	public final void setCustomMetricProcessWay(String customMetricProcessWay) {
		this.customMetricProcessWay = customMetricProcessWay;
	}



	/**
	 * @return the customMetricId
	 */
	public final String getCustomMetricId() {
		return customMetricId;
	}

	/**
	 * @param customMetricId the customMetricId to set
	 */
	public final void setCustomMetricId(String customMetricId) {
		this.customMetricId = customMetricId;
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
	 * @return the parameters
	 */
	public final ParameterValue[] getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public final void setParameters(ParameterValue[] parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the monitor
	 */
	public final boolean isMonitor() {
		return monitor;
	}

	/**
	 * @param monitor the monitor to set
	 */
	public final void setMonitor(boolean monitor) {
		this.monitor = monitor;
	}

	/**
	 * @return the freq
	 */
	public final String getFreq() {
		return freq;
	}

	/**
	 * @param freq the freq to set
	 */
	public final void setFreq(String freq) {
		this.freq = freq;
	}

	/**
	 * @return the instanceIds
	 */
	public final long getInstanceId() {
		return instanceId;
	}

	/**
	 * @param instanceIds the instanceIds to set
	 */
	public final void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}
}
