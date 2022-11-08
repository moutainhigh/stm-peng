package com.mainsteam.stm.portal.report.po;

import java.io.Serializable;

public class ReportTemplateDirectoryMetricPo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5043479429137591801L;
	private long reportDirectoryMetricId;
	private long reportTemplateDirectoryId;
	private int metricSortType;
	private String metricId;
	
	//期望值
	private long metricExpectValue;
	
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
