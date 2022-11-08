package com.mainsteam.stm.portal.inspect.web.vo;

public class InspectPlanSelfItemVo {
	// 表主键ID
	private int id;
	// 表外键，关联到inspect_plan表的主键
	private int inspectPlanId;
	// 自定义的名称
	private String inspectPlanSelfItemName;
	// 自定义的类型
	private int inspectPlanSelfItemType;
	// 自定义内容
	private String inspectPlanItemContent;

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

	public String getInspectPlanSelfItemName() {
		return inspectPlanSelfItemName;
	}

	public void setInspectPlanSelfItemName(String inspectPlanSelfItemName) {
		this.inspectPlanSelfItemName = inspectPlanSelfItemName;
	}

	public int getInspectPlanSelfItemType() {
		return inspectPlanSelfItemType;
	}

	public void setInspectPlanSelfItemType(int inspectPlanSelfItemType) {
		this.inspectPlanSelfItemType = inspectPlanSelfItemType;
	}

	public String getInspectPlanItemContent() {
		return inspectPlanItemContent;
	}

	public void setInspectPlanItemContent(String inspectPlanItemContent) {
		this.inspectPlanItemContent = inspectPlanItemContent;
	}

}
