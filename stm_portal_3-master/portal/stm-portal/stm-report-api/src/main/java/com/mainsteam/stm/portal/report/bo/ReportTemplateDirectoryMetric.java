package com.mainsteam.stm.portal.report.bo;

import java.io.Serializable;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;

public class ReportTemplateDirectoryMetric implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5043479429137591801L;
	private long reportDirectoryMetricId;
	private long reportTemplateDirectoryId;
	private String metricId;
	private String metricName;
	private MetricTypeEnum metricType;
	
	//指标排序方式0.升序1.降序 
	private int metricSortType;
	
	//期望值
	private long metricExpectValue;
	
	//当前阈值
	private int metricCurThreshold;
	
	//当前阈值变化趋势(0:越小越好.1:越大越好)
	private int metricThresholdDirection;
	
	//指标单位
	private String metricUnit;
	
	public String getMetricUnit() {
		return metricUnit;
	}
	public void setMetricUnit(String metricUnit) {
		this.metricUnit = metricUnit;
	}
	public int getMetricThresholdDirection() {
		return metricThresholdDirection;
	}
	public void setMetricThresholdDirection(int metricThresholdDirection) {
		this.metricThresholdDirection = metricThresholdDirection;
	}
	public int getMetricCurThreshold() {
		return metricCurThreshold;
	}
	public void setMetricCurThreshold(int metricCurThreshold) {
		this.metricCurThreshold = metricCurThreshold;
	}
	public long getReportDirectoryMetricId() {
		return reportDirectoryMetricId;
	}
	public void setReportDirectoryMetricId(long reportDirectoryMetricId) {
		this.reportDirectoryMetricId = reportDirectoryMetricId;
	}
	public long getReportTemplateDirectoryId() {
		return reportTemplateDirectoryId;
	}
	public void setReportTemplateDirectoryId(long reportTemplateDirectoryId) {
		this.reportTemplateDirectoryId = reportTemplateDirectoryId;
	}
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
	public MetricTypeEnum getMetricType() {
		return metricType;
	}
	public void setMetricType(MetricTypeEnum metricType) {
		this.metricType = metricType;
	}
	public int getMetricSortType() {
		return metricSortType;
	}
	public void setMetricSortType(int metricSortType) {
		this.metricSortType = metricSortType;
	}
	public long getMetricExpectValue() {
		return metricExpectValue;
	}
	public void setMetricExpectValue(long metricExpectValue) {
		this.metricExpectValue = metricExpectValue;
	}
	
}
