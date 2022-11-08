package com.mainsteam.stm.portal.report.po;

import java.io.Serializable;

public class ReportTemplateDirectoryPo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6200568757151012924L;
	private long reportTemplateDirectoryId;
	private long reportTemplateId;
	private String reportTemplateDirectoryName;
	private int reportTemplateDirectoryIsDetail;
	private String reportTemplateDirectoryResource;
	//报表模板目录TOPN数量
	private int reportTemplateDirectoryTopnCount;
	
	//报表模板目录类型
	private int reportTemplateDirectoryType;
	
	//报表模板目录致命
	private int reportTemplateDirectoryDeadly;
	
	//报表模板目录严重
	private int reportTemplateDirectorySerious;
	
	//报表模板目录告警
	private int reportTemplateDirectoryWarning;
	
	private String reportTemplateDirectoryCategoryId;
	
	private String reportTemplateDirectorySubResourceId;
	
	//报表类型为分析报表时0.平均值1.最大值2.最小值
	private int reportTemplateDirectoryMetricValueType;

	public long getReportTemplateDirectoryId() {
		return reportTemplateDirectoryId;
	}

	public void setReportTemplateDirectoryId(long reportTemplateDirectoryId) {
		this.reportTemplateDirectoryId = reportTemplateDirectoryId;
	}

	public long getReportTemplateId() {
		return reportTemplateId;
	}

	public void setReportTemplateId(long reportTemplateId) {
		this.reportTemplateId = reportTemplateId;
	}

	public String getReportTemplateDirectoryName() {
		return reportTemplateDirectoryName;
	}

	public void setReportTemplateDirectoryName(String reportTemplateDirectoryName) {
		this.reportTemplateDirectoryName = reportTemplateDirectoryName;
	}

	public int getReportTemplateDirectoryIsDetail() {
		return reportTemplateDirectoryIsDetail;
	}

	public void setReportTemplateDirectoryIsDetail(
			int reportTemplateDirectoryIsDetail) {
		this.reportTemplateDirectoryIsDetail = reportTemplateDirectoryIsDetail;
	}

	public String getReportTemplateDirectoryResource() {
		return reportTemplateDirectoryResource;
	}

	public void setReportTemplateDirectoryResource(
			String reportTemplateDirectoryResource) {
		this.reportTemplateDirectoryResource = reportTemplateDirectoryResource;
	}

	public int getReportTemplateDirectoryTopnCount() {
		return reportTemplateDirectoryTopnCount;
	}

	public void setReportTemplateDirectoryTopnCount(
			int reportTemplateDirectoryTopnCount) {
		this.reportTemplateDirectoryTopnCount = reportTemplateDirectoryTopnCount;
	}

	public int getReportTemplateDirectoryType() {
		return reportTemplateDirectoryType;
	}

	public void setReportTemplateDirectoryType(int reportTemplateDirectoryType) {
		this.reportTemplateDirectoryType = reportTemplateDirectoryType;
	}

	public int getReportTemplateDirectoryDeadly() {
		return reportTemplateDirectoryDeadly;
	}

	public void setReportTemplateDirectoryDeadly(int reportTemplateDirectoryDeadly) {
		this.reportTemplateDirectoryDeadly = reportTemplateDirectoryDeadly;
	}

	public int getReportTemplateDirectorySerious() {
		return reportTemplateDirectorySerious;
	}

	public void setReportTemplateDirectorySerious(int reportTemplateDirectorySerious) {
		this.reportTemplateDirectorySerious = reportTemplateDirectorySerious;
	}

	public int getReportTemplateDirectoryWarning() {
		return reportTemplateDirectoryWarning;
	}

	public void setReportTemplateDirectoryWarning(int reportTemplateDirectoryWarning) {
		this.reportTemplateDirectoryWarning = reportTemplateDirectoryWarning;
	}

	public String getReportTemplateDirectoryCategoryId() {
		return reportTemplateDirectoryCategoryId;
	}

	public void setReportTemplateDirectoryCategoryId(
			String reportTemplateDirectoryCategoryId) {
		this.reportTemplateDirectoryCategoryId = reportTemplateDirectoryCategoryId;
	}

	public String getReportTemplateDirectorySubResourceId() {
		return reportTemplateDirectorySubResourceId;
	}

	public void setReportTemplateDirectorySubResourceId(
			String reportTemplateDirectorySubResourceId) {
		this.reportTemplateDirectorySubResourceId = reportTemplateDirectorySubResourceId;
	}

	public int getReportTemplateDirectoryMetricValueType() {
		return reportTemplateDirectoryMetricValueType;
	}

	public void setReportTemplateDirectoryMetricValueType(
			int reportTemplateDirectoryMetricValueType) {
		this.reportTemplateDirectoryMetricValueType = reportTemplateDirectoryMetricValueType;
	}
	
	
	
}
