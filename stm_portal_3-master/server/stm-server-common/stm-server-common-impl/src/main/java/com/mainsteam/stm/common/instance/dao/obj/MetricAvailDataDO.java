/**
 * 
 */
package com.mainsteam.stm.common.instance.dao.obj;

/**
 * @author ziw
 *
 */
public class MetricAvailDataDO {
	
	private long instanceId;
	private String metricId;
	private String metricValue;

	/**
	 * 
	 */
	public MetricAvailDataDO() {
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
	 * @return the metricValue
	 */
	public final String getMetricValue() {
		return metricValue;
	}

	/**
	 * @param metricValue the metricValue to set
	 */
	public final void setMetricValue(String metricValue) {
		this.metricValue = metricValue;
	}
}
