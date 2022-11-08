package com.mainsteam.stm.portal.report.engine.databean;



import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.metric.report.MetricSummeryReportData;


public class ReportMetricData {
	
	private String metricId;
	private String metricName;
	private MetricTypeEnum metricType;
	
	//性能   汇总  详细
	MetricSummeryReportData metricData;

	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public MetricTypeEnum getMetricType() {
		return metricType;
	}

	public void setMetricType(MetricTypeEnum metricType) {
		this.metricType = metricType;
	}

	public MetricSummeryReportData getMetricData() {
		return metricData;
	}

	public void setMetricData(MetricSummeryReportData metricData) {
		this.metricData = metricData;
	}


}
