/**
 * 
 */
package com.mainsteam.stm.executor.obj;

/**
 * @author ziw
 *
 */
public class InstanceMetricExecuteParameter {

	/**
	 * 指标id
	 */
	private String metricId;
	
	/**
	 * 资源实例id
	 */
	private long resourceInstanceId;
	
	/**
	 * 
	 */
	public InstanceMetricExecuteParameter() {
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
	 * @return the resourceInstanceId
	 */
	public final long getResourceInstanceId() {
		return resourceInstanceId;
	}

	/**
	 * @param resourceInstanceId the resourceInstanceId to set
	 */
	public final void setResourceInstanceId(long resourceInstanceId) {
		this.resourceInstanceId = resourceInstanceId;
	}
}
