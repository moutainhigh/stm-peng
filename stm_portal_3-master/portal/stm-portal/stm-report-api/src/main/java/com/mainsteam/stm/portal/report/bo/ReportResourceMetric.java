package com.mainsteam.stm.portal.report.bo;

import java.io.Serializable;

public class ReportResourceMetric implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 975433013297270733L;
	private String id;
	private String name;
	private String unit;
	private int metricSort;
	private String metricExpectValue;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getMetricSort() {
		return metricSort;
	}
	public void setMetricSort(int metricSort) {
		this.metricSort = metricSort;
	}
	public String getMetricExpectValue() {
		return metricExpectValue;
	}
	public void setMetricExpectValue(String metricExpectValue) {
		this.metricExpectValue = metricExpectValue;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	@Override
	public int hashCode() {
		
		int result = 17;
		result = 37 * result + id.hashCode();
		result = 37 * result + name.hashCode();
		
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		
		if(!(obj instanceof ReportResourceMetric)){
			return false;
		}
		
		ReportResourceMetric metric = (ReportResourceMetric)obj;
		
		if(metric.getId().equals(id) && metric.getName().equals(name)){
			return true;
		}
		
		return false;
	}
	
}
