package com.mainsteam.stm.portal.report.po;

import java.io.Serializable;
import java.util.Date;

public class ReportTemplatePo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8645567305574120995L;
	private long reportTemplateId;
	private long reportTemplateCreateUserId;
	private Date reportTemplateCreateTime;
	private String reportTemplateName;
	private int reportTemplateType;
	private int reportTemplateCycle;
	private String reportTemplateBeginTime;
	private String reportTemplateEndTime;
	private int reportTemplateFirstGenerateTime;
	private int reportTemplateSecondGenerateTime;
	private int reportTemplateThirdGenerateTime;
	private int reportTemplateStatus;
	private int reportTemplateEmailStatus;
	private String reportTemplateEmailAddress;
	private String reportTemplateEmailFormat;
	//模型文件名
	private String reportTemplateModelName;
	//报表模板是否删除 0.未删除1.删除
	private int reportTemplateIsDelete;
	
	private long reportTemplateDomainId;
	
	public int getReportTemplateIsDelete() {
		return reportTemplateIsDelete;
	}
	public void setReportTemplateIsDelete(int reportTemplateIsDelete) {
		this.reportTemplateIsDelete = reportTemplateIsDelete;
	}
	public long getReportTemplateDomainId() {
		return reportTemplateDomainId;
	}
	public void setReportTemplateDomainId(long reportTemplateDomainId) {
		this.reportTemplateDomainId = reportTemplateDomainId;
	}
	public long getReportTemplateId() {
		return reportTemplateId;
	}
	public void setReportTemplateId(long reportTemplateId) {
		this.reportTemplateId = reportTemplateId;
	}
	public long getReportTemplateCreateUserId() {
		return reportTemplateCreateUserId;
	}
	public void setReportTemplateCreateUserId(long reportTemplateCreateUserId) {
		this.reportTemplateCreateUserId = reportTemplateCreateUserId;
	}
	public Date getReportTemplateCreateTime() {
		return reportTemplateCreateTime;
	}
	public void setReportTemplateCreateTime(Date reportTemplateCreateTime) {
		this.reportTemplateCreateTime = reportTemplateCreateTime;
	}
	public String getReportTemplateName() {
		return reportTemplateName;
	}
	public void setReportTemplateName(String reportTemplateName) {
		this.reportTemplateName = reportTemplateName;
	}
	public int getReportTemplateType() {
		return reportTemplateType;
	}
	public void setReportTemplateType(int reportTemplateType) {
		this.reportTemplateType = reportTemplateType;
	}
	public int getReportTemplateCycle() {
		return reportTemplateCycle;
	}
	public void setReportTemplateCycle(int reportTemplateCycle) {
		this.reportTemplateCycle = reportTemplateCycle;
	}
	public String getReportTemplateBeginTime() {
		return reportTemplateBeginTime;
	}
	public void setReportTemplateBeginTime(String reportTemplateBeginTime) {
		this.reportTemplateBeginTime = reportTemplateBeginTime;
	}
	public String getReportTemplateEndTime() {
		return reportTemplateEndTime;
	}
	public void setReportTemplateEndTime(String reportTemplateEndTime) {
		this.reportTemplateEndTime = reportTemplateEndTime;
	}
	public int getReportTemplateFirstGenerateTime() {
		return reportTemplateFirstGenerateTime;
	}
	public void setReportTemplateFirstGenerateTime(
			int reportTemplateFirstGenerateTime) {
		this.reportTemplateFirstGenerateTime = reportTemplateFirstGenerateTime;
	}
	public int getReportTemplateSecondGenerateTime() {
		return reportTemplateSecondGenerateTime;
	}
	public void setReportTemplateSecondGenerateTime(
			int reportTemplateSecondGenerateTime) {
		this.reportTemplateSecondGenerateTime = reportTemplateSecondGenerateTime;
	}
	public int getReportTemplateThirdGenerateTime() {
		return reportTemplateThirdGenerateTime;
	}
	public void setReportTemplateThirdGenerateTime(
			int reportTemplateThirdGenerateTime) {
		this.reportTemplateThirdGenerateTime = reportTemplateThirdGenerateTime;
	}
	public int getReportTemplateStatus() {
		return reportTemplateStatus;
	}
	public void setReportTemplateStatus(int reportTemplateStatus) {
		this.reportTemplateStatus = reportTemplateStatus;
	}
	public int getReportTemplateEmailStatus() {
		return reportTemplateEmailStatus;
	}
	public void setReportTemplateEmailStatus(int reportTemplateEmailStatus) {
		this.reportTemplateEmailStatus = reportTemplateEmailStatus;
	}
	public String getReportTemplateEmailAddress() {
		return reportTemplateEmailAddress;
	}
	public void setReportTemplateEmailAddress(String reportTemplateEmailAddress) {
		this.reportTemplateEmailAddress = reportTemplateEmailAddress;
	}
	public String getReportTemplateEmailFormat() {
		return reportTemplateEmailFormat;
	}
	public void setReportTemplateEmailFormat(String reportTemplateEmailFormat) {
		this.reportTemplateEmailFormat = reportTemplateEmailFormat;
	}
	public String getReportTemplateModelName() {
		return reportTemplateModelName;
	}
	public void setReportTemplateModelName(String reportTemplateModelName) {
		this.reportTemplateModelName = reportTemplateModelName;
	}
	
	
	
}
