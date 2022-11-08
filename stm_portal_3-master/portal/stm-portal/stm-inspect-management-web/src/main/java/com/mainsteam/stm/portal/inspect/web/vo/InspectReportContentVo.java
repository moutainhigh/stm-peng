package com.mainsteam.stm.portal.inspect.web.vo;

import java.util.List;

public class InspectReportContentVo {
	// 表主键ID
	private int id;
	// 外键，关联到inspect_ report表的主键
	private int inspectReportid;
	// 父ID指向当前表中的其它记录的ID
	private int inspectReportParentId;
	// 内容名称
	private String inspectReportItemName;
	// 内容描述
	private String inspectReportItemDescrible;
	// 巡检值
	private String inspectReportItemValue;
	// 参考值前缀
	private String inspectReportItemReferencePrefix;
	// 参考值后缀
	private String inspectR_reportItemReferenceSubfix;
	// 单位
	private String inspectReportItemUnit;
	// 情况概要
	private String inspectReportItemConditionDescrible;
	// 结果
	private String inspectReportItemResult;
	
	private List<InspectReportContentVo> children;

	/**
	 * @return the children
	 */
	public List<InspectReportContentVo> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<InspectReportContentVo> children) {
		this.children = children;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getInspectReportid() {
		return inspectReportid;
	}

	public void setInspectReportid(int inspectReportid) {
		this.inspectReportid = inspectReportid;
	}

	public int getInspectReportParentId() {
		return inspectReportParentId;
	}

	public void setInspectReportParentId(int inspectReportParentId) {
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

	public String getInspectReportItemReferencePrefix() {
		return inspectReportItemReferencePrefix;
	}

	public void setInspectReportItemReferencePrefix(
			String inspectReportItemReferencePrefix) {
		this.inspectReportItemReferencePrefix = inspectReportItemReferencePrefix;
	}

	public String getInspectR_reportItemReferenceSubfix() {
		return inspectR_reportItemReferenceSubfix;
	}

	public void setInspectR_reportItemReferenceSubfix(
			String inspectR_reportItemReferenceSubfix) {
		this.inspectR_reportItemReferenceSubfix = inspectR_reportItemReferenceSubfix;
	}

	public String getInspectReportItemUnit() {
		return inspectReportItemUnit;
	}

	public void setInspectReportItemUnit(String inspectReportItemUnit) {
		this.inspectReportItemUnit = inspectReportItemUnit;
	}

	public String getInspectReportItemConditionDescrible() {
		return inspectReportItemConditionDescrible;
	}

	public void setInspectReportItemConditionDescrible(
			String inspectReportItemConditionDescrible) {
		this.inspectReportItemConditionDescrible = inspectReportItemConditionDescrible;
	}

	public String getInspectReportItemResult() {
		return inspectReportItemResult;
	}

	public void setInspectReportItemResult(String inspectReportItemResult) {
		this.inspectReportItemResult = inspectReportItemResult;
	}

}
