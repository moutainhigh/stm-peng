package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;

public class BizDatabaseCapacityMetric implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -753965238321433969L;

	private long bizId;
	
	private String bizName;
	
	private String totalTableSpace;
	
	private String useTableSpace;
	
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

	public String getTotalTableSpace() {
		return totalTableSpace;
	}

	public void setTotalTableSpace(String totalTableSpace) {
		this.totalTableSpace = totalTableSpace;
	}

	public String getUseTableSpace() {
		return useTableSpace;
	}

	public void setUseTableSpace(String useTableSpace) {
		this.useTableSpace = useTableSpace;
	}

	public String getUseRate() {
		return useRate;
	}

	public void setUseRate(String useRate) {
		this.useRate = useRate;
	}
	
	
	
}
