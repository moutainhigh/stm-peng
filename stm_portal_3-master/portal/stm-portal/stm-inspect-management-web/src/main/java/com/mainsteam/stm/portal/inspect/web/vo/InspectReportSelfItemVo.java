package com.mainsteam.stm.portal.inspect.web.vo;

public class InspectReportSelfItemVo {
	// 表主键ID
	private int id;
	// 表外键，关联到inspect_report表的主键
	private int inspectReportId;
	// 自定义的名称
	private String inspectReportSelfItemName;
	// 自定义的类型
	private int inspectReportSelfItemType;
	// 自定义内容
	private String inspectReportItemContent;

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

	public String getInspectReportSelfItemName() {
		return inspectReportSelfItemName;
	}

	public void setInspectReportSelfItemName(String inspectReportSelfItemName) {
		this.inspectReportSelfItemName = inspectReportSelfItemName;
	}

	public int getInspectReportSelfItemType() {
		return inspectReportSelfItemType;
	}

	public void setInspectReportSelfItemType(int inspectReportSelfItemType) {
		this.inspectReportSelfItemType = inspectReportSelfItemType;
	}

	public String getInspectReportItemContent() {
		return inspectReportItemContent;
	}

	public void setInspectReportItemContent(String inspectReportItemContent) {
		this.inspectReportItemContent = inspectReportItemContent;
	}

}
