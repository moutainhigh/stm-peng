package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;

public class BizEditBo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -578190465591989682L;

	//id
	private long id;
	
	//业务ID
	private long bizId;
	
	//指标ID
	private String metricId;
	
	//指标值
	private String metricValue;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getBizId() {
		return bizId;
	}

	public void setBizId(long bizId) {
		this.bizId = bizId;
	}

	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public String getMetricValue() {
		return metricValue;
	}

	public void setMetricValue(String metricValue) {
		this.metricValue = metricValue;
	}
	
}
