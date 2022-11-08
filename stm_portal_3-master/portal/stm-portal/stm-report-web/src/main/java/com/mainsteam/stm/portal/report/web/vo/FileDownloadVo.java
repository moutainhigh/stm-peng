package com.mainsteam.stm.portal.report.web.vo;

import java.io.Serializable;

public class FileDownloadVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6533717851975476608L;

	private Long[] xmlFileID;
    private Long[] modelFileId;
    private Long reportTemplateId;
    private String[] reportCreateTimeStr;
    private String type;
    
    
	public String[] getReportCreateTimeStr() {
		return reportCreateTimeStr;
	}
	public void setReportCreateTimeStr(String[] reportCreateTimeStr) {
		this.reportCreateTimeStr = reportCreateTimeStr;
	}
	public Long getReportTemplateId() {
		return reportTemplateId;
	}
	public void setReportTemplateId(Long reportTemplateId) {
		this.reportTemplateId = reportTemplateId;
	}
	public Long[] getXmlFileID() {
		return xmlFileID;
	}
	public void setXmlFileID(Long[] xmlFileID) {
		this.xmlFileID = xmlFileID;
	}
	public Long[] getModelFileId() {
		return modelFileId;
	}
	public void setModelFileId(Long[] modelFileId) {
		this.modelFileId = modelFileId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
    
}
