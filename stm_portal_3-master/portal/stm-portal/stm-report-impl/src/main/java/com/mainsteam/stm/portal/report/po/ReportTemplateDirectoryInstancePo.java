package com.mainsteam.stm.portal.report.po;

import java.io.Serializable;

public class ReportTemplateDirectoryInstancePo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1788470175494281847L;
	private long reportDirectoryInstanceId;
	private long reportTemplateDirectoryId;
	private long instanceId;
	
	public long getReportDirectoryInstanceId() {
		return reportDirectoryInstanceId;
	}
	public void setReportDirectoryInstanceId(long reportDirectoryInstanceId) {
		this.reportDirectoryInstanceId = reportDirectoryInstanceId;
	}
	public long getReportTemplateDirectoryId() {
		return reportTemplateDirectoryId;
	}
	public void setReportTemplateDirectoryId(long reportTemplateDirectoryId) {
		this.reportTemplateDirectoryId = reportTemplateDirectoryId;
	}
	public long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}
	
	
	
}
