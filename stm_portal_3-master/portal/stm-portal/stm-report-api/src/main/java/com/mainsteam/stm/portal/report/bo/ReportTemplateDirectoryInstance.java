package com.mainsteam.stm.portal.report.bo;

import java.io.Serializable;
import java.util.List;

public class ReportTemplateDirectoryInstance implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1788470175494281847L;
	private long reportDirectoryInstanceId;
	private long reportTemplateDirectoryId;
	private long instanceId;
	private String instanceName;
	private String instanceType;
	private String instanceIP;

	private List<ReportTemplateDirectoryMetric> rtdMetricList;
	
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

	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	public String getInstanceType() {
		return instanceType;
	}
	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}
	public String getInstanceIP() {
		return instanceIP;
	}
	public void setInstanceIP(String instanceIP) {
		this.instanceIP = instanceIP;
	}
	public List<ReportTemplateDirectoryMetric> getRtdMetricList() {
		return rtdMetricList;
	}
	public void setRtdMetricList(List<ReportTemplateDirectoryMetric> rtdMetricList) {
		this.rtdMetricList = rtdMetricList;
	}

}
