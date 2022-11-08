package com.mainsteam.stm.portal.statist.bo;

import java.io.Serializable;

public class StatistQueryDataBo implements Serializable {

	private static final long serialVersionUID = -1910831377875556822L;
	private Long instanceId;
	private String instanceName;
	private String metricId;
	private String metricName;
	private String jsonData;

	public Long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

}
