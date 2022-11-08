package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;

public class MetricGroupVo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long instanceId;
	
	private long metricNameId;
	//指标名称
	private String metricName;
	//当前值
	private String currentVal;
	//采集时间
	private String lastCollTime;
	//指标类型
	private String type;
	public long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}
	public String getMetricName() {
		return metricName;
	}
	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}
	public String getCurrentVal() {
		return currentVal;
	}
	public void setCurrentVal(String currentVal) {
		this.currentVal = currentVal;
	}
	public String getLastCollTime() {
		return lastCollTime;
	}
	public void setLastCollTime(String lastCollTime) {
		this.lastCollTime = lastCollTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getMetricNameId() {
		return metricNameId;
	}
	public void setMetricNameId(long metricNameId) {
		this.metricNameId = metricNameId;
	}
}
