package com.mainsteam.stm.portal.report.bo;

import java.util.Date;

public class ReportBo {
	
	//报表ID
	private long reportListId;
	//报表模板ID
	private long reportTemplateId;
	//报表名称
	private String reportName;
	//0.未阅1.已阅
	private int reportStatus;
	//报表生成时间
	private Date reportGenerateTime;
	//报表XML数据
	private String reportXmlData;
	//报表模型名
	private String reportModelName;
	//创建用户姓名
	private String createUserName;
	//创建用户Id
	private String createUserId;
	
	private String reportTemplateType;
	private String reportTemplateCycle;
	private String reportTemplateCreateUser;
	private Long reportTemplateDomainId;
	
	
	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	public Long getReportTemplateDomainId() {
		return reportTemplateDomainId;
	}
	public void setReportTemplateDomainId(Long reportTemplateDomainId) {
		this.reportTemplateDomainId = reportTemplateDomainId;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public long getReportListId() {
		return reportListId;
	}
	public void setReportListId(long reportListId) {
		this.reportListId = reportListId;
	}
	public long getReportTemplateId() {
		return reportTemplateId;
	}
	public void setReportTemplateId(long reportTemplateId) {
		this.reportTemplateId = reportTemplateId;
	}
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public int getReportStatus() {
		return reportStatus;
	}
	public void setReportStatus(int reportStatus) {
		this.reportStatus = reportStatus;
	}
	public Date getReportGenerateTime() {
		return reportGenerateTime;
	}
	public void setReportGenerateTime(Date reportGenerateTime) {
		this.reportGenerateTime = reportGenerateTime;
	}
	public String getReportXmlData() {
		return reportXmlData;
	}
	public void setReportXmlData(String reportXmlData) {
		this.reportXmlData = reportXmlData;
	}
	public String getReportModelName() {
		return reportModelName;
	}
	public void setReportModelName(String reportModelName) {
		this.reportModelName = reportModelName;
	}
	public String getReportTemplateType() {
		return reportTemplateType;
	}
	public void setReportTemplateType(String reportTemplateType) {
		this.reportTemplateType = reportTemplateType;
	}
	public String getReportTemplateCycle() {
		return reportTemplateCycle;
	}
	public void setReportTemplateCycle(String reportTemplateCycle) {
		this.reportTemplateCycle = reportTemplateCycle;
	}
	public String getReportTemplateCreateUser() {
		return reportTemplateCreateUser;
	}
	public void setReportTemplateCreateUser(String reportTemplateCreateUser) {
		this.reportTemplateCreateUser = reportTemplateCreateUser;
	}
		
	

}
