package com.mainsteam.stm.portal.report.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DefaultAlarmMetric implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7952730068535195818L;
	private List<ReportResourceMetric> metricData;

	public DefaultAlarmMetric(){
		this.metricData = new ArrayList<ReportResourceMetric>();
		
		ReportResourceMetric metricAlarm = new ReportResourceMetric();
		metricAlarm.setId("alarmCount");
		metricAlarm.setName("告警数量");
		this.metricData.add(metricAlarm);
		
		ReportResourceMetric metricStatus = new ReportResourceMetric();
		metricStatus.setId("statusDistribution");
		metricStatus.setName("状态分布");
		this.metricData.add(metricStatus);
		
		ReportResourceMetric metricLevel = new ReportResourceMetric();
		metricLevel.setId("levelDistribution");
		metricLevel.setName("级别分布");
		this.metricData.add(metricLevel);
		
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
