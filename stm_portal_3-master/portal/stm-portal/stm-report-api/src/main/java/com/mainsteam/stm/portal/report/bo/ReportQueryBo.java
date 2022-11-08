package com.mainsteam.stm.portal.report.bo;


public class ReportQueryBo {

	private Long[] reportTemplateIdArr;
	private Long reportTemplateType;
	private String reportDateStartSelect;
	private String reportDateEndSelect;
	private String reportName;
	private Long[] userIdArr;
	private int reportType;
	
	private String[] reportQueryStatus;
	private String[] reportTemplateQueryCycle;
	private String  reportQueryCreateUserName;
	private Long[] reportTemplateDomainId;
	
	private String order;
	
	public Long[] getReportTemplateDomainId() {
		return reportTemplateDomainId;
	}
	public void setReportTemplateDomainId(Long[] reportTemplateDomainId) {
		this.reportTemplateDomainId = reportTemplateDomainId;
	}
	public int getReportType() {
		return reportType;
	}
	public void setReportType(int reportType) {
		this.reportType = reportType;
	}
	public String getReportQueryCreateUserName() {
		return reportQueryCreateUserName;
	}
	public void setReportQueryCreateUserName(String reportQueryCreateUserName) {
		this.reportQueryCreateUserName = reportQueryCreateUserName;
	}
	public String[] getReportQueryStatus() {
		return reportQueryStatus;
	}
	public void setReportQueryStatus(String[] reportQueryStatus) {
		this.reportQueryStatus = reportQueryStatus;
	}
	public String[] getReportTemplateQueryCycle() {
		return reportTemplateQueryCycle;
	}
	public void setReportTemplateQueryCycle(String[] reportTemplateQueryCycle) {
		this.reportTemplateQueryCycle = reportTemplateQueryCycle;
	}
	public Long[] getUserIdArr() {
		return userIdArr;
	}
	public void setUserIdArr(Long[] userIdArr) {
		this.userIdArr = userIdArr;
	}
	public Long[] getReportTemplateIdArr() {
		return reportTemplateIdArr;
	}
	public void setReportTemplateIdArr(Long[] reportTemplateIdArr) {
		this.reportTemplateIdArr = reportTemplateIdArr;
	}
	public Long getReportTemplateType() {
		return reportTemplateType;
	}
	public void setReportTemplateType(Long reportTemplateType) {
		this.reportTemplateType = reportTemplateType;
	}
	public String getReportDateStartSelect() {
		return reportDateStartSelect;
	}
	public void setReportDateStartSelect(String reportDateStartSelect) {
		this.reportDateStartSelect = reportDateStartSelect;
	}
	public String getReportDateEndSelect() {
		return reportDateEndSelect;
	}
	public void setReportDateEndSelect(String reportDateEndSelect) {
		this.reportDateEndSelect = reportDateEndSelect;
	}
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	
}
