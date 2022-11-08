package com.mainsteam.stm.portal.inspect.bo;

public class InspectReportBasicBo {
	// 表主键ID
	private Long id;
	// 域名称
	private String inspectReportDomain;
	// 巡检人名称
	private String inspectReportInspector;
	// 是否显示报告生成时间
	private Boolean inspectReportProduceTimeShow;
	// 是否显示报告最后编辑时间
	private Boolean inspectReportModifyTimeShow;
	// 是否显示最后编辑人
	private Boolean inspectReportModifiorShow;
	// 资源类型名称
	private String inspectReportResourceName;
	// 业务名称
	private String inspectReportBusinessName;
	// 状态（是否提交）
	private Boolean inspectReportStatus;
	// 创建人
	private String inspectReportTaskCreator;
	// 报告提交时间
	private String inspectReportSubmitTime;
	// 巡检报告名称
	private String inspectReportName;
	// 巡检计划名称
	private String inspectReportPlanName;
	// 报告生成时间
	private String inspectReportProduceTime;
	// 报告最后编辑时间
	private String editTime;
	// 报告最后编剧人
	private String editUserName;
	// 是否允许修改报告
	private boolean edit = false;
	// 计划id
	private Long planId;
	// 创建人id
	private Long creatorId;
	// 巡检人id
	private Long inspectorId;
	//判断这条记录的显示状态
	private String userType;

	//是否是巡检人
	private Boolean isInspector;
	//是否是创建人
	private Boolean isCreateUser;
	
	public Boolean getIsInspector() {
		return isInspector;
	}

	public void setIsInspector(Boolean isInspector) {
		this.isInspector = isInspector;
	}

	public Boolean getIsCreateUser() {
		return isCreateUser;
	}

	public void setIsCreateUser(Boolean isCreateUser) {
		this.isCreateUser = isCreateUser;
	}

	public Long getPlanId() {
		return planId;
	}

	public void setPlanId(Long planId) {
		this.planId = planId;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public Long getInspectorId() {
		return inspectorId;
	}

	public void setInspectorId(Long inspectorId) {
		this.inspectorId = inspectorId;
	}

	public boolean getEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getInspectReportProduceTimeShow() {
		return inspectReportProduceTimeShow;
	}

	public void setInspectReportProduceTimeShow(
			Boolean inspectReportProduceTimeShow) {
		this.inspectReportProduceTimeShow = inspectReportProduceTimeShow;
	}

	public Boolean getInspectReportModifyTimeShow() {
		return inspectReportModifyTimeShow;
	}

	public void setInspectReportModifyTimeShow(
			Boolean inspectReportModifyTimeShow) {
		this.inspectReportModifyTimeShow = inspectReportModifyTimeShow;
	}

	public Boolean getInspectReportModifiorShow() {
		return inspectReportModifiorShow;
	}

	public void setInspectReportModifiorShow(Boolean inspectReportModifiorShow) {
		this.inspectReportModifiorShow = inspectReportModifiorShow;
	}

	public String getEditTime() {
		return editTime;
	}

	public void setEditTime(String editTime) {
		this.editTime = editTime;
	}

	public String getEditUserName() {
		return editUserName;
	}

	public void setEditUserName(String editUserName) {
		this.editUserName = editUserName;
	}

	public String getInspectReportDomain() {
		return inspectReportDomain;
	}

	public void setInspectReportDomain(String inspectReportDomain) {
		this.inspectReportDomain = inspectReportDomain;
	}

	public String getInspectReportInspector() {
		return inspectReportInspector;
	}

	public void setInspectReportInspector(String inspectReportInspector) {
		this.inspectReportInspector = inspectReportInspector;
	}

	public String getInspectReportResourceName() {
		return inspectReportResourceName;
	}

	public void setInspectReportResourceName(String inspectReportResourceName) {
		this.inspectReportResourceName = inspectReportResourceName;
	}

	public String getInspectReportBusinessName() {
		return inspectReportBusinessName;
	}

	public void setInspectReportBusinessName(String inspectReportBusinessName) {
		this.inspectReportBusinessName = inspectReportBusinessName;
	}

	public Boolean getInspectReportStatus() {
		return inspectReportStatus;
	}

	public void setInspectReportStatus(Boolean inspectReportStatus) {
		this.inspectReportStatus = inspectReportStatus;
	}

	public String getInspectReportTaskCreator() {
		return inspectReportTaskCreator;
	}

	public void setInspectReportTaskCreator(String inspectReportTaskCreator) {
		this.inspectReportTaskCreator = inspectReportTaskCreator;
	}

	public String getInspectReportSubmitTime() {
		return inspectReportSubmitTime;
	}

	public void setInspectReportSubmitTime(String inspectReportSubmitTime) {
		this.inspectReportSubmitTime = inspectReportSubmitTime;
	}

	public String getInspectReportName() {
		return inspectReportName;
	}

	public void setInspectReportName(String inspectReportName) {
		this.inspectReportName = inspectReportName;
	}

	public String getInspectReportPlanName() {
		return inspectReportPlanName;
	}

	public void setInspectReportPlanName(String inspectReportPlanName) {
		this.inspectReportPlanName = inspectReportPlanName;
	}

	public String getInspectReportProduceTime() {
		return inspectReportProduceTime;
	}

	public void setInspectReportProduceTime(String inspectReportProduceTime) {
		this.inspectReportProduceTime = inspectReportProduceTime;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
	
}
