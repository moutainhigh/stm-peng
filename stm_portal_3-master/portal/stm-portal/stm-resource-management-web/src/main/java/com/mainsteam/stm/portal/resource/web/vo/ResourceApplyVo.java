package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;

public class ResourceApplyVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1962030977012722866L;

	private long instanceId;
	//cpu利用率
	private double cpuRateValue;
	//cpu频率
	private double CPUMHz;
	//cpu类型
	private String CPUModel;
	//
	private String  metricType;
	
	private String resourceType;
	
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public String getMetricType() {
		return metricType;
	}
	public void setMetricType(String metricType) {
		this.metricType = metricType;
	}
	public long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}
	public double getCpuRateValue() {
		return cpuRateValue;
	}
	public void setCpuRateValue(double cpuRateValue) {
		this.cpuRateValue = cpuRateValue;
	}
	public double getCPUMHz() {
		return CPUMHz;
	}
	public void setCPUMHz(double cPUMHz) {
		CPUMHz = cPUMHz;
	}
	public String getCPUModel() {
		return CPUModel;
	}
	public void setCPUModel(String cPUModel) {
		CPUModel = cPUModel;
	}
	
	
	
}
