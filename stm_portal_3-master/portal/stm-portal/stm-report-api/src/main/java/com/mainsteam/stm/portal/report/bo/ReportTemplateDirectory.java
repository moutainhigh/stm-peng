package com.mainsteam.stm.portal.report.bo;

import java.io.Serializable;
import java.util.List;

public class ReportTemplateDirectory implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6090005699828727677L;
	//报表模板目录ID
	private long reportTemplateDirectoryId;
	//报表模板ID
	private long reportTemplateId;
	//报表模板目录名称
	private String reportTemplateDirectoryName;
	//报表模板目录是否详细信息:0.是1.否
	private int reportTemplateDirectoryIsDetail;
	//报表模板目录资源类型
	private String reportTemplateDirectoryResource;
	
	//报表模板目录TOPN数量
	private int reportTemplateDirectoryTopnCount;
	
	//报表模板目录类型
	private int reportTemplateDirectoryType;
	
	//报表模板目录致命
	private int reportTemplateDirectoryDeadly;
	
	//报表模板目录严重
	private int reportTemplateDirectorySerious;
	
	//报表模板目录警告
	private int reportTemplateDirectoryWarning;
	
	private String reportTemplateDirectoryCategoryId;
	
	private String reportTemplateDirectorySubResourceId;
	
	//报表类型为分析报表时0.平均值1.最大值2.最小值,为topn时1.性能2.告警
	private int reportTemplateDirectoryMetricValueType;
	
	private List<ReportTemplateDirectoryInstance> directoryInstanceList;
	private List<ReportTemplateDirectoryMetric> directoryMetricList;
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
	public List<ReportTemplateDirectoryInstance> getDirectoryInstanceList() {
		return directoryInstanceList;
	}
	public void setDirectoryInstanceList(
			List<ReportTemplateDirectoryInstance> directoryInstanceList) {
		this.directoryInstanceList = directoryInstanceList;
	}
	public List<ReportTemplateDirectoryMetric> getDirectoryMetricList() {
		return directoryMetricList;
	}
	public void setDirectoryMetricList(
			List<ReportTemplateDirectoryMetric> directoryMetricList) {
		this.directoryMetricList = directoryMetricList;
	}
	
	
}
