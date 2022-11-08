package com.mainsteam.stm.portal.inspect.bo;

public class InspectReportResultsSettingBo {
	// 表主键ID
	private Long id;
	// 外键，关联到inspect_report表的主键
	private Long inspectReportId;
	// 结论名称
	private String inspectReportSummeriseName;
	// 结论描述
	private String inspectReportSumeriseDescrible;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getInspectReportId() {
		return inspectReportId;
	}

	public void setInspectReportId(Long inspectReportId) {
		this.inspectReportId = inspectReportId;
	}

	public String getInspectReportSummeriseName() {
		return inspectReportSummeriseName;
	}

	public void setInspectReportSummeriseName(String inspectReportSummeriseName) {
		this.inspectReportSummeriseName = inspectReportSummeriseName;
	}

	public String getInspectReportSumeriseDescrible() {
		return inspectReportSumeriseDescrible;
	}

	public void setInspectReportSumeriseDescrible(
			String inspectReportSumeriseDescrible) {
		this.inspectReportSumeriseDescrible = inspectReportSumeriseDescrible;
	}

}
