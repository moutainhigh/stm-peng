package com.mainsteam.stm.webService.performance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "MetricValue")
public class MetricValue {
	
	@XmlElement(name = "metricId")
	private String metricId;
	
	@XmlElement(name = "collectTime")
	private String collectTime;
	
	@XmlElement(name = "value")
	private String value;

	@XmlElement(name = "metricState")
	private String metricState;
	
	
	public String getMetricState() {
		return metricState;
	}

	public void setMetricState(String metricState) {
		this.metricState = metricState;
	}

	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public String getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(String collectTime) {
		this.collectTime = collectTime;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
	
}
