package com.mainsteam.stm.portal.inspect.bo;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class InspectReportContentBo {
	// 表主键ID
	private Long id;
	// 外键，关联到inspect_ report表的主键
	private Long inspectReportid;
	// 父ID指向当前表中的其它记录的ID
	private Long inspectReportParentId;
	// 内容名称
	private String inspectReportItemName;
	// 内容描述
	private String inspectReportItemDescrible;
	// 巡检值
	private String inspectReportItemValue;
	// 参考值前缀
	private String reportItemReferencePrefix;
	// 参考值后缀
	private String reportItemReferenceSubfix;
	// 单位
	private String inspectReportItemUnit;
	// 情况概要
	private String reportItemConditionDescrible; 
	// 结果
	private Boolean inspectReportItemResult = false;
	// 资源id
	private Long resourceId;
	// 是否是检查项
	private boolean edit = false;
	// 是否是指标作为巡检项
	private boolean indicatorAsItem = false;
	// 指标是否是性能指标
	private boolean iscappoint = false;
	// 1:手动，2：自动
	private int type;
	@XStreamImplicit(itemFieldName="children")
	private List<InspectReportContentBo> children;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public boolean isIndicatorAsItem() {
		return indicatorAsItem;
	}

	public void setIndicatorAsItem(boolean indicatorAsItem) {
		this.indicatorAsItem = indicatorAsItem;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public List<InspectReportContentBo> getChildren() {
		return children;
	}

	public void setChildren(List<InspectReportContentBo> children) {
		this.children = children;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getInspectReportid() {
		return inspectReportid;
	}

	public void setInspectReportid(Long inspectReportid) {
		this.inspectReportid = inspectReportid;
	}

	public Long getInspectReportParentId() {
		return inspectReportParentId;
	}

	public void setInspectReportParentId(Long inspectReportParentId) {
		this.inspectReportParentId = inspectReportParentId;
	}

	public String getInspectReportItemName() {
		return inspectReportItemName;
	}

	public void setInspectReportItemName(String inspectReportItemName) {
		this.inspectReportItemName = inspectReportItemName;
	}

	public String getInspectReportItemDescrible() {
		return inspectReportItemDescrible;
	}

	public void setInspectReportItemDescrible(String inspectReportItemDescrible) {
		this.inspectReportItemDescrible = inspectReportItemDescrible;
	}

	public String getInspectReportItemValue() {
		return inspectReportItemValue;
	}

	public void setInspectReportItemValue(String inspectReportItemValue) {
		this.inspectReportItemValue = inspectReportItemValue;
	}
	public String getReportItemReferencePrefix() {
		return reportItemReferencePrefix;
	}

	public void setReportItemReferencePrefix(String reportItemReferencePrefix) {
		this.reportItemReferencePrefix = reportItemReferencePrefix;
	}

	public String getReportItemReferenceSubfix() {
		return reportItemReferenceSubfix;
	}

	public void setReportItemReferenceSubfix(String reportItemReferenceSubfix) {
		this.reportItemReferenceSubfix = reportItemReferenceSubfix;
	}

	public String getInspectReportItemUnit() {
		return inspectReportItemUnit;
	}

	public void setInspectReportItemUnit(String inspectReportItemUnit) {
		this.inspectReportItemUnit = inspectReportItemUnit;
	}

	public String getReportItemConditionDescrible() {
		return reportItemConditionDescrible;
	}

	public void setReportItemConditionDescrible(String reportItemConditionDescrible) {
		this.reportItemConditionDescrible = reportItemConditionDescrible;
	}

	public Boolean getInspectReportItemResult() {
		return inspectReportItemResult;
	}

	public void setInspectReportItemResult(Boolean inspectReportItemResult) {
		this.inspectReportItemResult = inspectReportItemResult;
	}

	public boolean isIscappoint() {
		return iscappoint;
	}

	public void setIscappoint(boolean iscappoint) {
		this.iscappoint = iscappoint;
	}

}
