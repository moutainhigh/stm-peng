package com.mainsteam.stm.common.metric.report;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.obj.TimePeriod;

public class MetricSummeryReportQuery{
	private List<Long> instanceIDes;
	private List<MetricWithTypeForReport> metricIDes;
	private MetricSummaryType summaryType;
	private List<TimePeriod> timePeriods;
	/**
	 * 只针对 MetricSummaryType.SIX情况
	 */
	Date[] weeks;
	
	public MetricSummaryType getSummaryType() {
		return summaryType;
	}
	public void setSummaryType(MetricSummaryType summaryType) {
		this.summaryType = summaryType;
	}
	public List<MetricWithTypeForReport> getMetricIDes() {
		return metricIDes;
	}
	public void setMetricIDes(List<MetricWithTypeForReport> metricIDes) {
		this.metricIDes = metricIDes;
	}
	public List<Long> getInstanceIDes() {
		return instanceIDes;
	}
	public void setInstanceIDes(List<Long> instanceIDes) {
		this.instanceIDes = instanceIDes;
	}
	public Date[] getWeeks() {
		return weeks;
	}
	public void setWeeks(Date[] weeks) {
		this.weeks = weeks;
	}
	public List<TimePeriod> getTimePeriods() {
		return timePeriods;
	}
	public void setTimePeriods(List<TimePeriod> timePeriods) {
		this.timePeriods = timePeriods;
	}
}
