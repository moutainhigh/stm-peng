package com.mainsteam.stm.portal.inspect.web.vo;

public class InspectReportResultsSettingVo {
	// 表主键ID
	private int id;
	// 外键，关联到inspect_report表的主键
	private int inspectReportId;
	// 结论名称
	private String inspectReportSummeriseName;
	// 结论描述
	private String InspectReportSumeriseDescrible;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getInspectReportId() {
		return inspectReportId;
	}

	public void setInspectReportId(int inspectReportId) {
		this.inspectReportId = inspectReportId;
	}

	public String getInspectReportSummeriseName() {
		return inspectReportSummeriseName;
	}

	public void setInspectReportSummeriseName(String inspectReportSummeriseName) {
		this.inspectReportSummeriseName = inspectReportSummeriseName;
	}

	public String getInspectReportSumeriseDescrible() {
		return InspectReportSumeriseDescrible;
	}

	public void setInspectReportSumeriseDescrible(
			String inspectReportSumeriseDescrible) {
		InspectReportSumeriseDescrible = inspectReportSumeriseDescrible;
	}

}
