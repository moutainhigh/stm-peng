package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;

public class BizCapMetricBo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1070177399993140501L;

	//id
	private long id;
	
	//业务ID
	private long bizId;
	
	//资源实例ID
	private long instanceId;
	
	//指标ID
	private String metricId;

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

	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}
	
}
