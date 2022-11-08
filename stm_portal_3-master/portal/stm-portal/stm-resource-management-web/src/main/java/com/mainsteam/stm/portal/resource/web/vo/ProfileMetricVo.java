package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;

public class ProfileMetricVo extends ProfileMetric implements Serializable {
	

	private static final long serialVersionUID = 1928841388863406257L;
	//指标名称
	private String metricName;
	//指标类型
	private MetricTypeEnum metricTypeEnum;
	
	
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
	
	

}
