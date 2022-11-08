package com.mainsteam.stm.metric.obj;

import java.io.Serializable;
import java.util.Date;

import com.mainsteam.stm.caplib.dict.FrequentEnum;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;

public class CustomMetricInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8165166296016052134L;
	

	private String id;
	
	private String name;
	
	private String unit;
	
	private MetricTypeEnum style;
	
	private boolean alert;
	
	private boolean monitor;
	
	private FrequentEnum freq;

	private int flapping;
	
	private Date updateTime;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUnit() {
		return unit;
	}

	public MetricTypeEnum getStyle() {
		return style;
	}

	public boolean isAlert() {
		return alert;
	}

	public boolean isMonitor() {
		return monitor;
	}


	public int getFlapping() {
		return flapping;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setStyle(MetricTypeEnum style) {
		this.style = style;
	}

	public void setAlert(boolean alert) {
		this.alert = alert;
	}

	public void setMonitor(boolean monitor) {
		this.monitor = monitor;
	}

	public void setFlapping(int flapping) {
		this.flapping = flapping;
	}

	public FrequentEnum getFreq() {
		return freq;
	}

	public void setFreq(FrequentEnum freq) {
		this.freq = freq;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
