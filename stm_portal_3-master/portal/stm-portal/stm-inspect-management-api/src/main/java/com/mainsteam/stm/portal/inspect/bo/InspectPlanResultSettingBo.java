package com.mainsteam.stm.portal.inspect.bo;

public class InspectPlanResultSettingBo {
	// 表主键ID
	private Long id;
	// 外键，关联到inspect_plan表的主键
	private Long inspectPlanId;
	// 结论名称
	private String inspectPlanSummeriseName;
	// 结论描述
	private String inspectPlanSumeriseDescrible;

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
