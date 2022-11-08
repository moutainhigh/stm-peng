package com.mainsteam.stm.portal.inspect.bo;
 
import java.io.Serializable;

public class InspectReportSelfItemBo implements Serializable {

	private static final long serialVersionUID = -6465252977708110582L;
	
	// 表主键ID
	private long id;
	// 表外键，关联到inspect_report表的主键
	private long inspectReportId;
	// 自定义的名称
	private String inspectReportSelfItemName;
	// 自定义的类型
	private int inspectReportSelfItemType;
	// 自定义内容
	private String inspectReportItemContent;
	
	public InspectReportSelfItemBo() {
		
	}
	
	/**
	 * @param inspectReportId
	 * @param inspectReportSelfItemName
	 * @param inspectReportSelfItemType
	 * @param inspectReportItemContent
	 */
	public InspectReportSelfItemBo(long id, long inspectReportId,
			String inspectReportSelfItemName, int inspectReportSelfItemType,
			String inspectReportItemContent) {
		super();
		this.id = id;
		this.inspectReportId = inspectReportId;
		this.inspectReportSelfItemName = inspectReportSelfItemName;
		this.inspectReportSelfItemType = inspectReportSelfItemType;
		this.inspectReportItemContent = inspectReportItemContent;
	}



	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getInspectReportId() {
		return inspectReportId;
	}

	public void setInspectReportId(long inspectReportId) {
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
