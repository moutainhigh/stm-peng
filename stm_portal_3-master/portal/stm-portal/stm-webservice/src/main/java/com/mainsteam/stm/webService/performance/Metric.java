package com.mainsteam.stm.webService.performance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Metric")
public class Metric {
	
	@XmlElement(name = "metricId")
	private String metricId;
	
	@XmlElement(name = "metricName")
	private String metricName;
	
	@XmlElement(name = "unit")
	private String unit;
	
	@XmlElement(name = "metricType")
	private String metricType;

	
	public String getMetricType() {
		return metricType;
	}

	public void setMetricType(String metricType) {
		this.metricType = metricType;
	}

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

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
}
