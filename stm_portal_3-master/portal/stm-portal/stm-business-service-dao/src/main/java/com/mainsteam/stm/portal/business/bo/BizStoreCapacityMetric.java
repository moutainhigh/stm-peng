package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;

public class BizStoreCapacityMetric implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -753965238321433969L;

	private long bizId;
	
	private String bizName;
	
	private String totalStore;
	
	private String useStore;
	
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

	public String getTotalStore() {
		return totalStore;
	}

	public void setTotalStore(String totalStore) {
		this.totalStore = totalStore;
	}

	public String getUseStore() {
		return useStore;
	}

	public void setUseStore(String useStore) {
		this.useStore = useStore;
	}

	public String getUseRate() {
		return useRate;
	}

	public void setUseRate(String useRate) {
		this.useRate = useRate;
	}
	
	
	
}
