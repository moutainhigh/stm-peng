package com.mainsteam.stm.portal.inspect.bo;

public class InspectReportConditionBo {
	private String inspectReportProduceStartTime;
	private String inspectReportProduceEndTime;
	private String inspectReportInspector;
	private String inspectReportPlanName;
	private String inspectReportCreator;
	private String[] inspectReportDomain;
	private Long userId;
	private Integer[] status;
	private String reportName;
	// 创建人id
	private Long createUserId;
	// 巡检人id
	private Long inspectorId;

	private String authorityUserId;
	private String[] authorityDomainIds;

	public String getAuthorityUserId() {
		return authorityUserId;
	}

	public void setAuthorityUserId(String authorityUserId) {
		this.authorityUserId = authorityUserId;
	}

	public String[] getAuthorityDomainIds() {
		return authorityDomainIds;
	}

	public void setAuthorityDomainIds(String[] authorityDomainIds) {
		this.authorityDomainIds = authorityDomainIds;
	}

	public Long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public Long getInspectorId() {
		return inspectorId;
	}

	public void setInspectorId(Long inspectorId) {
		this.inspectorId = inspectorId;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public Integer[] getStatus() {
		return status;
	}

	public void setStatus(Integer[] status) {
		this.status = status;
	}

	public String getInspectReportProduceStartTime() {
		return inspectReportProduceStartTime;
	}

	public void setInspectReportProduceStartTime(
			String inspectReportProduceStartTime) {
		this.inspectReportProduceStartTime = inspectReportProduceStartTime;
	}

	public String getInspectReportProduceEndTime() {
		return inspectReportProduceEndTime;
	}

	public void setInspectReportProduceEndTime(
			String inspectReportProduceEndTime) {
		this.inspectReportProduceEndTime = inspectReportProduceEndTime;
	}

	public String getInspectReportInspector() {
		return inspectReportInspector;
	}

	public void setInspectReportInspector(String inspectReportInspector) {
		this.inspectReportInspector = inspectReportInspector;
	}

	public String getInspectReportPlanName() {
		return inspectReportPlanName;
	}

	public void setInspectReportPlanName(String inspectReportPlanName) {
		this.inspectReportPlanName = inspectReportPlanName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getInspectReportCreator() {
		return inspectReportCreator;
	}

	public void setInspectReportCreator(String inspectReportCreator) {
		this.inspectReportCreator = inspectReportCreator;
	}

	public String[] getInspectReportDomain() {
		return inspectReportDomain;
	}

	public void setInspectReportDomain(String[] inspectReportDomain) {
		this.inspectReportDomain = inspectReportDomain;
	}

}
