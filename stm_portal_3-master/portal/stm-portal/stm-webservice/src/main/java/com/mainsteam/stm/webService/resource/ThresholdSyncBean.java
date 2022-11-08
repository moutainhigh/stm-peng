package com.mainsteam.stm.webService.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ThresholdSyncBean")
public class ThresholdSyncBean {

	@XmlElement(name = "metricId", required = true)
	private String metricId;
	
	@XmlElement(name = "minorValue", required = true)
	private String minorValue;
	
	@XmlElement(name = "majorValue", required = true)
	private String majorValue;

	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public String getMinorValue() {
		return minorValue;
	}

	public void setMinorValue(String minorValue) {
		this.minorValue = minorValue;
	}

	public String getMajorValue() {
		return majorValue;
	}

	public void setMajorValue(String majorValue) {
		this.majorValue = majorValue;
	}

	
	
}
