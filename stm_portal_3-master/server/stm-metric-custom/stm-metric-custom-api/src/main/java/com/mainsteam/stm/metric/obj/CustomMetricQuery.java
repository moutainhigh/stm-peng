package com.mainsteam.stm.metric.obj;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;

public class CustomMetricQuery {

	private String customMetricName;
	
	private MetricTypeEnum customMetricStyle;
	
	private Boolean isAlert;
	
	private Boolean isMonitor;

	public String getCustomMetricName() {
		return customMetricName;
	}

	public MetricTypeEnum getCustomMetricStyle() {
		return customMetricStyle;
	}

	public Boolean getIsAlert() {
		return isAlert;
	}

	public Boolean getIsMonitor() {
		return isMonitor;
	}

	public void setCustomMetricName(String customMetricName) {
		this.customMetricName = customMetricName;
	}

	public void setCustomMetricStyle(MetricTypeEnum customMetricStyle) {
		this.customMetricStyle = customMetricStyle;
	}

	public void setIsAlert(Boolean isAlert) {
		this.isAlert = isAlert;
	}

	public void setIsMonitor(Boolean isMonitor) {
		this.isMonitor = isMonitor;
	}

}
