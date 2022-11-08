package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;

public class ResourceApplyCPUBo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7973594893843659534L;
	
	private long instanceId;
	//cpu利用率
	private double cpuRateValue;
	//cpu频率
	private double CPUMHz;
	//cpu类型
	private String CPUModel;
	
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
