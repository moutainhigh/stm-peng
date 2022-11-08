package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;
import java.util.List;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;


public class ProfileMetricBo extends ProfileMetric implements Serializable {
	
	private static final long serialVersionUID = 468339895071571337L;
	//指标名称
	private String metricName;
	//指标类型
	private MetricTypeEnum metricTypeEnum;
	
	private List<MetricFrequentBo> supportFrequentList;
	
	//指标内阈值单位
	private String unit;
	
	private Long displayOrder;
	
	public Long getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Long displayOrder) {
		this.displayOrder = displayOrder;
	}
	public String getMetricName() {
		return metricName;
	}
	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}
	public MetricTypeEnum getMetricTypeEnum() {
		return metricTypeEnum;
	}
	public void setMetricTypeEnum(MetricTypeEnum metricTypeEnum) {
		this.metricTypeEnum = metricTypeEnum;
	}
	public List<MetricFrequentBo> getSupportFrequentList() {
		return supportFrequentList;
	}
	public void setSupportFrequentList(List<MetricFrequentBo> supportFrequentList) {
		this.supportFrequentList = supportFrequentList;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	

}
