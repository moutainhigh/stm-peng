package com.mainsteam.stm.simple.manager.workbench.report.bo;

public class MetricData {

	/**
	 * 指标ＩＤ
	 */
	private String metricId;
	/**
	 * 指标名称
	 */
	private String metricName;
	/**
	 * 指标值
	 */
	private Object metridValue;
	public String getMetricId() {
		return metricId;
	}
	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}
	public String getMetricName() {
		return metricName;
	}
	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}
	public Object getMetridValue() {
		return metridValue;
	}
	public void setMetridValue(Object metridValue) {
		this.metridValue = metridValue;
	}
	public MetricData(String metricId, String metricName, Object metridValue) {
		super();
		this.metricId = metricId;
		this.metricName = metricName;
		this.metridValue = metridValue;
	}
	public MetricData(String metricName, Object metridValue) {
		super();
		this.metricName = metricName;
		this.metridValue = metridValue;
	}
	public MetricData() {
		super();
	}
	
	
}
