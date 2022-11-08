package com.mainsteam.stm.metric.dao.pojo;

public class CustomMetricThresholdDO {

	private String metricId;
	
	private String metricState;
	
	private String expressionOperator;
	
	private String thresholdValue;
	
	private String expressionDesc;
	
	private String alarmTemplate;
	
	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public String getMetricState() {
		return metricState;
	}

	public String getExpressionOperator() {
		return expressionOperator;
	}

	public String getThresholdValue() {
		return thresholdValue;
	}

	public String getExpressionDesc() {
		return expressionDesc;
	}


	public void setMetricState(String metricState) {
		this.metricState = metricState;
	}

	public void setExpressionOperator(String expressionOperator) {
		this.expressionOperator = expressionOperator;
	}

	public void setThresholdValue(String thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	public void setExpressionDesc(String expressionDesc) {
		this.expressionDesc = expressionDesc;
	}

	public String getAlarmTemplate() {
		return alarmTemplate;
	}

	public void setAlarmTemplate(String alarmTemplate) {
		this.alarmTemplate = alarmTemplate;
	}
}
