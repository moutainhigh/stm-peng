/**
 * 
 */
package com.mainsteam.stm.executor.obj;

import com.mainsteam.stm.common.metric.obj.MetricData;

/**
 * @author ziw
 *
 */
public class DiscoveryMetricData extends MetricData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5632776881955342616L;
	
	private String indexPropertyName;
	
	private String indexPropertyValue;

	/**
	 * 
	 */
	public DiscoveryMetricData() {
	}

	/**
	 * @return the indexPropertyName
	 */
	public final String getIndexPropertyName() {
		return indexPropertyName;
	}

	/**
	 * @param indexPropertyName the indexPropertyName to set
	 */
	public final void setIndexPropertyName(String indexPropertyName) {
		this.indexPropertyName = indexPropertyName;
	}

	/**
	 * @return the indexPropertyValue
	 */
	public final String getIndexPropertyValue() {
		return indexPropertyValue;
	}

	/**
	 * @param indexPropertyValue the indexPropertyValue to set
	 */
	public final void setIndexPropertyValue(String indexPropertyValue) {
		this.indexPropertyValue = indexPropertyValue;
	}
}
