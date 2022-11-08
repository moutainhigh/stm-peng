package com.mainsteam.stm.portal.report.po;

import java.io.Serializable;
import java.util.Date;

public class ReportPo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1760556520299420340L;
	private long reportListId;
	private long reportTemplateId;
	private String reportName;
	private int reportStatus;
	private Date reportGenerateTime;
	private String reportXmlData;
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
