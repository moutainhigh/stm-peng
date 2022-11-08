package com.mainsteam.stm.portal.inspect.bo;

import java.util.List;

public class Routine {
//	private String inspectPlanReportProduceTimeShow;
	private String reportProduceTimeShow;
	private String reportModifyTimeShow;
	private String reportProduceTime;
	private String reportModifyTime;
//	private String inspectPlanReportModifyTimeShow;
	private String inspectPlanReportModifiorShow;
	private String inspectReportResourceNameShow;
	private String inspectReportBusinessNameShow;
	private String inspectReportResourceName;
	private String inspectReportBusinessName;
	private Long inspector;
	private String inspectorName;
	private String resourceName;
	private String businessName;
	private List<InspectPlanSelfItemBo> selfItems;
	private String reportName;
	private String inspectReportResourceType;

	public String getInspectReportResourceType() {
		return inspectReportResourceType;
	}

	public void setInspectReportResourceType(String inspectReportResourceType) {
		this.inspectReportResourceType = inspectReportResourceType;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public Long getInspector() {
		return inspector;
	}

	public void setInspector(Long inspector) {
		this.inspector = inspector;
	}

	public String getInspectorName() {
		return inspectorName;
	}

	public void setInspectorName(String inspectorName) {
		this.inspectorName = inspectorName;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getReportProduceTimeShow() {
		return reportProduceTimeShow;
	}

	public void setReportProduceTimeShow(String reportProduceTimeShow) {
		this.reportProduceTimeShow = reportProduceTimeShow;
	}

	public String getReportModifyTimeShow() {
		return reportModifyTimeShow;
	}

	public void setReportModifyTimeShow(String reportModifyTimeShow) {
		this.reportModifyTimeShow = reportModifyTimeShow;
	}

	public String getInspectPlanReportModifiorShow() {
		return inspectPlanReportModifiorShow;
	}

	public void setInspectPlanReportModifiorShow(
			String inspectPlanReportModifiorShow) {
		this.inspectPlanReportModifiorShow = inspectPlanReportModifiorShow;
	}

	public String getInspectReportResourceNameShow() {
		return inspectReportResourceNameShow;
	}

	public void setInspectReportResourceNameShow(
			String inspectReportResourceNameShow) {
		this.inspectReportResourceNameShow = inspectReportResourceNameShow;
	}

	public String getInspectReportBusinessNameShow() {
		return inspectReportBusinessNameShow;
	}

	public void setInspectReportBusinessNameShow(
			String inspectReportBusinessNameShow) {
		this.inspectReportBusinessNameShow = inspectReportBusinessNameShow;
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

	public List<InspectPlanSelfItemBo> getSelfItems() {
		return selfItems;
	}

	public void setSelfItems(List<InspectPlanSelfItemBo> selfItems) {
		this.selfItems = selfItems;
	}

	public String getReportProduceTime() {
		return reportProduceTime;
	}

	public void setReportProduceTime(String reportProduceTime) {
		this.reportProduceTime = reportProduceTime;
	}

	public String getReportModifyTime() {
		return reportModifyTime;
	}

	public void setReportModifyTime(String reportModifyTime) {
		this.reportModifyTime = reportModifyTime;
	}

}
