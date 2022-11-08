/**
 * 
 */
package com.mainsteam.stm.common.metric.obj;

import java.io.Serializable;

/**
 * @author ziw
 *
 */
public class AvailMetricData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8100938586959178097L;
	
	private long instanceId;
	private String metricId;
	private String metricValue;
	/**
	 * 
	 */
	public AvailMetricData() {
	}
	
	/**
	 * @param instanceId
	 * @param metricId
	 * @param metricValue
	 */
	public AvailMetricData(long instanceId, String metricId, String metricValue) {
		this.instanceId = instanceId;
		this.metricId = metricId;
		this.metricValue = metricValue;
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
