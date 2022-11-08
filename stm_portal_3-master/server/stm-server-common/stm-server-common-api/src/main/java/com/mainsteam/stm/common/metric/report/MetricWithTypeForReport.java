package com.mainsteam.stm.common.metric.report;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;

public class MetricWithTypeForReport {
	
	private String metricID;
	private MetricTypeEnum type;
	
	public MetricWithTypeForReport(String metricID,MetricTypeEnum metricType) {
		this.metricID=metricID;
		this.type=metricType;
	}
	public String getMetricID() {
		return metricID;
	}
	public void setMetricID(String metricID) {
		this.metricID = metricID;
	}
	public MetricTypeEnum getType() {
		return type;
	}
	public void setType(MetricTypeEnum type) {
		this.type = type;
	}
}
