package com.mainsteam.stm.portal.inspect.web.vo;

public class InspectPlanContentVo {
	// 表主键ID
	private int id;
	// 外键，关联到inspect_plan表的主键
	private int inspectPlanId;
	// 父ID，指向本表其它记录的ID
	private int inspectPlanParentId;
	// 内容名称
	private String inspectPlanItemName;
	// 内容描述
	private String InspectPlanItemDescrible;
	// 巡检值
	private String inspectPlanItemValue;
	// 参考值前缀
	private String inspectPlanItemReferencePrefix;
	// 参考值后缀
	private String inspectPlanItemReferenceSubfix;
	// 单位
	private String inspectPlanItemUnit;
	// 情况概要
	private String inspectPlanItemConditionDescrible;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getInspectPlanId() {
		return inspectPlanId;
	}

	public void setInspectPlanId(int inspectPlanId) {
		this.inspectPlanId = inspectPlanId;
	}

	public int getInspectPlanParentId() {
		return inspectPlanParentId;
	}

	public void setInspectPlanParentId(int inspectPlanParentId) {
		this.inspectPlanParentId = inspectPlanParentId;
	}

	public String getInspectPlanItemName() {
		return inspectPlanItemName;
	}

	public void setInspectPlanItemName(String inspectPlanItemName) {
		this.inspectPlanItemName = inspectPlanItemName;
	}

	public String getInspectPlanItemDescrible() {
		return InspectPlanItemDescrible;
	}

	public void setInspectPlanItemDescrible(String inspectPlanItemDescrible) {
		InspectPlanItemDescrible = inspectPlanItemDescrible;
	}

	public String getInspectPlanItemValue() {
		return inspectPlanItemValue;
	}

	public void setInspectPlanItemValue(String inspectPlanItemValue) {
		this.inspectPlanItemValue = inspectPlanItemValue;
	}

	public String getInspectPlanItemReferencePrefix() {
		return inspectPlanItemReferencePrefix;
	}

	public void setInspectPlanItemReferencePrefix(
			String inspectPlanItemReferencePrefix) {
		this.inspectPlanItemReferencePrefix = inspectPlanItemReferencePrefix;
	}

	public String getInspectPlanItemReferenceSubfix() {
		return inspectPlanItemReferenceSubfix;
	}

	public void setInspectPlanItemReferenceSubfix(
			String inspectPlanItemReferenceSubfix) {
		this.inspectPlanItemReferenceSubfix = inspectPlanItemReferenceSubfix;
	}

	public String getInspectPlanItemUnit() {
		return inspectPlanItemUnit;
	}

	public void setInspectPlanItemUnit(String inspectPlanItemUnit) {
		this.inspectPlanItemUnit = inspectPlanItemUnit;
	}

	public String getInspectPlanItemConditionDescrible() {
		return inspectPlanItemConditionDescrible;
	}

	public void setInspectPlanItemConditionDescrible(
			String inspectPlanItemConditionDescrible) {
		this.inspectPlanItemConditionDescrible = inspectPlanItemConditionDescrible;
	}

}
