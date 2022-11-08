package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;

public class BizBandwidthCapacityMetric implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -753965238321433969L;

	private long bizId;
	
	private String bizName;
	
	private String totalFlow;
	
	private String useFlow;
	
	private String useRate;

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

	public String getTotalFlow() {
		return totalFlow;
	}

	public void setTotalFlow(String totalFlow) {
		this.totalFlow = totalFlow;
	}

	public String getUseFlow() {
		return useFlow;
	}

	public void setUseFlow(String useFlow) {
		this.useFlow = useFlow;
	}

	public String getUseRate() {
		return useRate;
	}

	public void setUseRate(String useRate) {
		this.useRate = useRate;
	}
	
	
	
}
