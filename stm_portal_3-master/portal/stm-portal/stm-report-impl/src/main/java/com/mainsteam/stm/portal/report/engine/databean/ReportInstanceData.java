package com.mainsteam.stm.portal.report.engine.databean;

import java.util.Map;

public class ReportInstanceData {

	//资源实例ID
	private long instanceId;
	//资源名称
	private String instanceName;
	//资源类型
	private String instanceType;
	//资源的IP
	private String instanceIP;
	
	private String createTime;
	
	//性能   汇总  详细
	Map<String,ReportMetricData> metricDataMap;

	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}

	public String getInstanceIP() {
		return instanceIP;
	}

	public void setInstanceIP(String instanceIP) {
		this.instanceIP = instanceIP;
	}

	public Map<String, ReportMetricData> getMetricDataMap() {
		return metricDataMap;
	}

	public void setMetricDataMap(Map<String, ReportMetricData> metricDataMap) {
		this.metricDataMap = metricDataMap;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	
}
