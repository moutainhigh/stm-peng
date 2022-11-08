package com.mainsteam.stm.webService.resource;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ResultInstanceBean")
public class ResultInstanceBean {
	
	@XmlElement(name = "instanceId", required = true)
	private long instanceId;
	
	@XmlElement(name = "metricIds", required = true)
	private List<String> metricIds;

	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public List<String> getMetricIds() {
		return metricIds;
	}

	public void setMetricIds(List<String> metricIds) {
		this.metricIds = metricIds;
	}
	
	
	
}
