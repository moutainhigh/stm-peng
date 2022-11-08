/**
 * 
 */
package com.mainsteam.stm.executor.obj;

import java.util.HashMap;
import java.util.Map;

/**
 * 指标执行需要绑定的参数
 * 
 * @author ziw
 * 
 */
public class MetricDiscoveryParameter {
	
	public static final String SYSTEM_OID_NAME = "sysObjectID";

	/**
	 * 指标id
	 */
	private String metricId;

	/**
	 * 资源id
	 */
	private String resourceId;

	/**
	 * 发现信息
	 */
	private Map<String, String> discoveryInfo;

	/**
	 * 
	 */
	public MetricDiscoveryParameter() {
		discoveryInfo = new HashMap<>();
	}

	public Map<String, String> getDiscoveryInfo() {
		return discoveryInfo;
	}

	public void setDiscoveryInfo(Map<String, String> discoveryInfo) {
		this.discoveryInfo = discoveryInfo;
	}

	public void addDiscoveryInfo(String discoveryKey, String discoveryValue) {
		this.discoveryInfo.put(discoveryKey, discoveryValue);
	}

	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
}
