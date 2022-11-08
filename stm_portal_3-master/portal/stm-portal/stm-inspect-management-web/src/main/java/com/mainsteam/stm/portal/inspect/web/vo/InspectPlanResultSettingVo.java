package com.mainsteam.stm.portal.inspect.web.vo;

public class InspectPlanResultSettingVo {
	// 表主键ID
	private int id;
	// 外键，关联到inspect_plan表的主键
	private int inspectPlanId;
	// 结论名称
	private String inspectPlanSummeriseName;
	// 结论描述
	private String inspectPlanSumeriseDescrible;

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

	public String getInspectPlanSummeriseName() {
		return inspectPlanSummeriseName;
	}

	public void setInspectPlanSummeriseName(String inspectPlanSummeriseName) {
		this.inspectPlanSummeriseName = inspectPlanSummeriseName;
	}

	public String getInspectPlanSumeriseDescrible() {
		return inspectPlanSumeriseDescrible;
	}

	public void setInspectPlanSumeriseDescrible(
			String inspectPlanSumeriseDescrible) {
		this.inspectPlanSumeriseDescrible = inspectPlanSumeriseDescrible;
	}

}
