package com.mainsteam.stm.webService.performance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ResourcePerformance")
public class ResourcePerformance {
	
	@XmlElement(name = "perfId")
	private String perfId;
	
	@XmlElement(name = "source")
	private String source;
	
	@XmlElement(name = "resourceId")
	private String resourceId;
	
	@XmlElement(name = "metricValues")
	private MetricValue[] metricValues;
	
	public String getPerfId() {
		return perfId;
	}
	public void setPerfId(String perfId) {
		this.perfId = perfId;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public MetricValue[] getMetricValues() {
		return metricValues;
	}
	public void setMetricValues(MetricValue[] metricValues) {
		this.metricValues = metricValues;
	}
	
}
