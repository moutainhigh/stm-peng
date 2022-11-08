/**
 * 
 */
package com.mainsteam.stm.executor.obj;

import com.mainsteam.stm.pluginprocessor.ParameterValue;

/**
 * @author ziw
 */
public class CustomMetricPluginParameter implements Cloneable {

	/**
	 * 资源实例id
	 */
	private long instanceId;

	/**
	 * 自定义指标id
	 */
	private String customMetricId;

	/**
	 * 插件id
	 */
	private String pluginId;

	/**
	 * 插件执行参数列表
	 */
	private ParameterValue[] parameters;
	
	/**
	 * 数据处理方式类
	 */
	private String dataProcessClass;

	/**
	 * 
	 */
	public CustomMetricPluginParameter() {
	}
	

	/**
	 * @return the dataProcessClass
	 */
	public final String getDataProcessClass() {
		return dataProcessClass;
	}



	/**
	 * @param dataProcessClass the dataProcessClass to set
	 */
	public final void setDataProcessClass(String dataProcessClass) {
		this.dataProcessClass = dataProcessClass;
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
	
	@Override
	public CustomMetricPluginParameter clone(){
		
		CustomMetricPluginParameter n = new CustomMetricPluginParameter();
		n.customMetricId = customMetricId;
		n.dataProcessClass = dataProcessClass;
		n.instanceId = instanceId;
		n.pluginId = pluginId;
		ParameterValue[] values = null;
		if(this.parameters!=null){
			values = new ParameterValue[parameters.length];
			for (int i = 0; i < values.length; i++) {
				values[i] = new ParameterValue();
				values[i].setKey(parameters[i].getKey());
				values[i].setValue(parameters[i].getValue());
				values[i].setType(parameters[i].getType());
			}
			n.parameters = values;
		}
		return n;
	}
}
