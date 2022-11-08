package com.mainsteam.stm.portal.report.bo;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("reportTemplateData")
public class ReportTemplateData {
	
	
	//类型1.性能报告2.告警统计3.TOPN报告4.可用性报告5.综合报告
	@XStreamAsAttribute
	private String type;
	
	//报表名字
	@XStreamAsAttribute
	private String name;
	
	//时间范围
	@XStreamAsAttribute
	private String timeScope;
	
	//时间周期(1.日报2.周报3.月报)
	@XStreamAsAttribute
	private String cycle;
	
	@XStreamImplicit(itemFieldName="reportDirectory")
	private List<ReportDirectory> reportDirectory;

	public List<ReportDirectory> getReportDirectory() {
		return reportDirectory;
	}

	public void setReportDirectory(List<ReportDirectory> reportDirectory) {
		this.reportDirectory = reportDirectory;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTimeScope() {
		return timeScope;
	}

	public void setTimeScope(String timeScope) {
		this.timeScope = timeScope;
	}

	public String getCycle() {
		return cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}
	
}
