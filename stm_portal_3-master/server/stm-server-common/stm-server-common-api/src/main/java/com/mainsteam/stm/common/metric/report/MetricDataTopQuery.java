package com.mainsteam.stm.common.metric.report;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.obj.TimePeriod;


public class MetricDataTopQuery{
	private List<Long> instanceIDes;
	private String metricID;
	private boolean orderByMax=true;
	private List<TimePeriod> timePeriods;
	private Integer limit;
	private MetricSummaryType summaryType;
	

	public String getMetricID() {
		return metricID;
	}
	public void setMetricID(String metricID) {
		this.metricID = metricID;
	}
	public boolean isOrderByMax() {
		return orderByMax;
	}
	public void setOrderByMax(boolean orderByMax) {
		this.orderByMax = orderByMax;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public List<Long> getInstanceIDes() {
		return instanceIDes;
	}
	public void setInstanceIDes(List<Long> instanceIDes) {
		this.instanceIDes = instanceIDes;
	}
	public MetricSummaryType getSummaryType() {
		return summaryType;
	}
	public void setSummaryType(MetricSummaryType summaryType) {
		this.summaryType = summaryType;
	}
	public List<TimePeriod> getTimePeriods() {
		return timePeriods;
	}
	public void setTimePeriods(List<TimePeriod> timePeriods) {
		this.timePeriods = timePeriods;
	}
}
