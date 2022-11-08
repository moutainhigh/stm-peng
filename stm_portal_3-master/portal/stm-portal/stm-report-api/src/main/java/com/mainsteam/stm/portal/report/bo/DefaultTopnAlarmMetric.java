package com.mainsteam.stm.portal.report.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DefaultTopnAlarmMetric implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2491724930913288378L;
	private List<ReportResourceMetric> metricData;

	public DefaultTopnAlarmMetric(){
		this.metricData = new ArrayList<ReportResourceMetric>();
		
		ReportResourceMetric metricAlarmAllCount = new ReportResourceMetric();
		metricAlarmAllCount.setId("alarmAllCount");
		metricAlarmAllCount.setName("告警总数");
		metricAlarmAllCount.setMetricSort(1);
		this.metricData.add(metricAlarmAllCount);
		
		ReportResourceMetric metricDeadly = new ReportResourceMetric();
		metricDeadly.setId("deadlyCount");
		metricDeadly.setName("致命告警数");
		metricDeadly.setMetricSort(1);
		this.metricData.add(metricDeadly);
		
		ReportResourceMetric metricSerious = new ReportResourceMetric();
		metricSerious.setId("seriousCount");
		metricSerious.setName("严重告警数");
		metricSerious.setMetricSort(1);
		this.metricData.add(metricSerious);
		
		ReportResourceMetric metricWarning = new ReportResourceMetric();
		metricWarning.setId("warningCount");
		metricWarning.setName("警告告警数");
		metricWarning.setMetricSort(1);
		this.metricData.add(metricWarning);
		
	}

	public List<ReportResourceMetric> getMetricData() {
		return metricData;
	}
	
	public String getNameById(String id){
		
		for(ReportResourceMetric metric : this.metricData){
			if(metric.getId().equals(id)){
				return metric.getName();
			}
		}
		
		return null;
		
	}
	
}
