package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;

public class BizCalculateCapacityMetric implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -753965238321433969L;

	private long bizId;
	
	private String bizName;
	
	private String cpuRate;

	public long getBizId() {
		return bizId;
	}

	public void setBizId(long bizId) {
		this.bizId = bizId;
	}

	public String getBizName() {
		return bizName;
	}

	public void setBizName(String bizName) {
		this.bizName = bizName;
	}

	public String getCpuRate() {
		return cpuRate;
	}

	public void setCpuRate(String cpuRate) {
		this.cpuRate = cpuRate;
	}
	
	
	
}
