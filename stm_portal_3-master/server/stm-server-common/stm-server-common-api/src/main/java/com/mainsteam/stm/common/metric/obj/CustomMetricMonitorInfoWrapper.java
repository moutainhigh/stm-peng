/**
 * 
 */
package com.mainsteam.stm.common.metric.obj;

import java.io.Serializable;
import java.util.Map;

/**
 * @author ziw
 * 
 */
public class CustomMetricMonitorInfoWrapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4634297607253032815L;

	private CustomMetricMonitorInfo[] customMetricMonitorInfos;

	/**
	 * key:资源实例id,value:profileId
	 */
	private Map<Long, Long> instanceProfileMap;

	/**
	 * @return the customMetricMonitorInfos
	 */
	public final CustomMetricMonitorInfo[] getCustomMetricMonitorInfos() {
		return customMetricMonitorInfos;
	}

	/**
	 * @param customMetricMonitorInfos
	 *            the customMetricMonitorInfos to set
	 */
	public final void setCustomMetricMonitorInfos(
			CustomMetricMonitorInfo[] customMetricMonitorInfos) {
		this.customMetricMonitorInfos = customMetricMonitorInfos;
	}

	/**
	 * @return the instanceProfileMap
	 */
	public final Map<Long, Long> getInstanceProfileMap() {
		return instanceProfileMap;
	}

	/**
	 * @param instanceProfileMap
	 *            the instanceProfileMap to set
	 */
	public final void setInstanceProfileMap(Map<Long, Long> instanceProfileMap) {
		this.instanceProfileMap = instanceProfileMap;
	}
}
