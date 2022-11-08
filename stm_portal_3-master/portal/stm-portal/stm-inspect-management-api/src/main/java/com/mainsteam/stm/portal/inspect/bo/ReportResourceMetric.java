package com.mainsteam.stm.portal.inspect.bo;

import java.io.Serializable;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;

public class ReportResourceMetric implements Serializable,Comparable<ReportResourceMetric> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 975433013297270733L;
	private String id;
	private String name;
	private String unit;
	private MetricTypeEnum style;

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

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public MetricTypeEnum getStyle() {
		return style;
	}

	public void setStyle(MetricTypeEnum style) {
		this.style = style;
	}

	@Override
	public int hashCode() {
		return id == null ? 0 : id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ReportResourceMetric) {
			ReportResourceMetric metric = (ReportResourceMetric) obj;
			return id == metric.id || id.equals(metric.id);
		}
		return false;
	}

	
	@Override
	public int compareTo(ReportResourceMetric arg0) {
		
		return this.getName().compareTo(arg0.getName());
	}
	
	
}
