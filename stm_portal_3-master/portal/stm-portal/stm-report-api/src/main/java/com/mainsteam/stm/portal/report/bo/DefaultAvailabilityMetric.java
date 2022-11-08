package com.mainsteam.stm.portal.report.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DefaultAvailabilityMetric implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7952730068535195818L;
	private List<ReportResourceMetric> metricData;

	public DefaultAvailabilityMetric(){
		this.metricData = new ArrayList<ReportResourceMetric>();
		
		ReportResourceMetric metricAvailability = new ReportResourceMetric();
		metricAvailability.setId("availabilityRatio");
		metricAvailability.setName("可用性比率(%)");
		this.metricData.add(metricAvailability);
		
		ReportResourceMetric metricInvalidCount = new ReportResourceMetric();
		metricInvalidCount.setId("metricInvalidCount");
		metricInvalidCount.setName("不可用次数(次)");
		this.metricData.add(metricInvalidCount);
		
		ReportResourceMetric metricInvalid = new ReportResourceMetric();
		metricInvalid.setId("invalidTime");
		metricInvalid.setName("不可用时长(小时)");
		this.metricData.add(metricInvalid);
		
		ReportResourceMetric metricMttr = new ReportResourceMetric();
		metricMttr.setId("mttr");
		metricMttr.setName("MTTR(小时)");
		this.metricData.add(metricMttr);
		
		ReportResourceMetric metricMtbf = new ReportResourceMetric();
		metricMtbf.setId("mtbf");
		metricMtbf.setName("MTBF(小时)");
		this.metricData.add(metricMtbf);
		
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
