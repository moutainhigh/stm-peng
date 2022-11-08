package com.mainsteam.stm.portal.report.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ReportTemplate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7250181610643145147L;
	//ID index
	private long reportTemplateId;
	//创建人
	private long reportTemplateCreateUserId;
	private String reportTemplateCreateUserName;
	//创建时间
	private Date reportTemplateCreateTime;
	//模板名称
	private String reportTemplateName;
	//模板类型,1.性能报告2.告警统计3.TOPN报告4.可用性报告5.趋势报告.6.分析报告7.业务报表.100.综合报表
	private int reportTemplateType;
	//报表周期：1.日报2.周报3.月报
	private int reportTemplateCycle;
	//开始时间,若为周报则存哪几天
	private String reportTemplateBeginTime;
	//结束时间
	private String reportTemplateEndTime;
	
	//第一生成时间:0.当前1.下一个
	private int reportTemplateFirstGenerateTime;
	//第二生成时间
	private int reportTemplateSecondGenerateTime;
	//第三生成时间:范围为0~24
	private int reportTemplateThirdGenerateTime;
	
	//是否启用:0.启用1.停用
	private int reportTemplateStatus;
	//邮件订阅是否启用:0.启用1.停用
	private int reportTemplateEmailStatus;
	//收件人地址:多个email地址用";"(分号)隔开
	private String reportTemplateEmailAddress;
	//邮件附件类型:1.Excel2.Word3.PDF(逗号隔开)
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

	private List<ReportTemplateDirectory> directoryList;

	public long getReportTemplateId() {
		return reportTemplateId;
	}

	public void setReportTemplateId(long reportTemplateId) {
		this.reportTemplateId = reportTemplateId;
	}

	public long getReportTemplateCreateUserId() {
		return reportTemplateCreateUserId;
	}
	
	public String getReportTemplateCreateUserName() {
		return reportTemplateCreateUserName;
	}

	public void setReportTemplateCreateUserName(String reportTemplateCreateUserName) {
		this.reportTemplateCreateUserName = reportTemplateCreateUserName;
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

	public List<ReportTemplateDirectory> getDirectoryList() {
		return directoryList;
	}

	public void setDirectoryList(List<ReportTemplateDirectory> directoryList) {
		this.directoryList = directoryList;
	}
	
	
}
