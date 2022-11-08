package com.mainsteam.stm.metric.dao.pojo;

import java.util.Date;

public class CustomMetricDO {

	private String customMetricId;
	
	private String customMetricName;
	
	private String customMetricUnit;
	
	private String customMetricStyle;
	
	private String isMonitor;
	
	private String isAlert;
	
	private String freq;
	
	private int flapping;
	
	private Date updateTime;
	
	public String getCustomMetricId() {
		return customMetricId;
	}

	public String getCustomMetricName() {
		return customMetricName;
	}

	public String getCustomMetricUnit() {
		return customMetricUnit;
	}

	public String getCustomMetricStyle() {
		return customMetricStyle;
	}

	public String getIsMonitor() {
		return isMonitor;
	}

	public String getIsAlert() {
		return isAlert;
	}

	public String getFreq() {
		return freq;
	}

	public int getFlapping() {
		return flapping;
	}

	public void setCustomMetricId(String customMetricId) {
		this.customMetricId = customMetricId;
	}

	public void setCustomMetricName(String customMetricName) {
		this.customMetricName = customMetricName;
	}

	public void setCustomMetricUnit(String customMetricUnit) {
		this.customMetricUnit = customMetricUnit;
	}

	public void setCustomMetricStyle(String customMetricStyle) {
		this.customMetricStyle = customMetricStyle;
	}

	public void setIsMonitor(String isMonitor) {
		this.isMonitor = isMonitor;
	}

	public void setIsAlert(String isAlert) {
		this.isAlert = isAlert;
	}

	public void setFreq(String freq) {
		this.freq = freq;
	}

	public void setFlapping(int flapping) {
		this.flapping = flapping;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
