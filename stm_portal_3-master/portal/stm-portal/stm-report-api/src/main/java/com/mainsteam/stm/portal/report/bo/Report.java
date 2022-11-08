package com.mainsteam.stm.portal.report.bo;

import java.io.Serializable;
import java.util.Date;


public class Report implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1760556520299420340L;
	//报表ID
	private long reportListId;
	//报表模板ID
	private long reportTemplateId;
	//报表名称
	private String reportName;
	//0.未阅1.已阅
	private int reportStatus;
	//报表生成时间
	private Date reportGenerateTime;
	//报表XML数据
	private String reportXmlData;
	//报表模型名
	private String reportModelName;
	
	public long getReportListId() {
		return reportListId;
	}
	public void setReportListId(long reportListId) {
		this.reportListId = reportListId;
	}
	public long getReportTemplateId() {
		return reportTemplateId;
	}
	public void setReportTemplateId(long reportTemplateId) {
		this.reportTemplateId = reportTemplateId;
	}
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public int getReportStatus() {
		return reportStatus;
	}
	public void setReportStatus(int reportStatus) {
		this.reportStatus = reportStatus;
	}
	public Date getReportGenerateTime() {
		return reportGenerateTime;
	}
	public void setReportGenerateTime(Date reportGenerateTime) {
		this.reportGenerateTime = reportGenerateTime;
	}
	public String getReportXmlData() {
		return reportXmlData;
	}
	public void setReportXmlData(String reportXmlData) {
		this.reportXmlData = reportXmlData;
	}
	public String getReportModelName() {
		return reportModelName;
	}
	public void setReportModelName(String reportModelName) {
		this.reportModelName = reportModelName;
	}
	
	
	
}
