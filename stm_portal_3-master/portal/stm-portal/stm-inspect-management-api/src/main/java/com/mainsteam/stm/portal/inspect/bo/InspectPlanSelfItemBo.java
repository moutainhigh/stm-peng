package com.mainsteam.stm.portal.inspect.bo;

public class InspectPlanSelfItemBo {
	// 表主键ID
	private Long id;
	// 表外键，关联到inspect_plan表的主键
	private Long inspectPlanId;
	// 自定义的名称
	private String inspectPlanSelfItemName;
	// 自定义的类型
	private int inspectPlanSelfItemType;
	// 自定义内容
	private String inspectPlanItemContent;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getInspectPlanId() {
		return inspectPlanId;
	}

	public void setInspectPlanId(Long inspectPlanId) {
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
