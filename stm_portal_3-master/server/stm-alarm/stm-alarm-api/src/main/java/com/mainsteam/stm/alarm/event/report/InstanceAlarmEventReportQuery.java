package com.mainsteam.stm.alarm.event.report;

import java.util.List;
import com.mainsteam.stm.obj.TimePeriod;

public class InstanceAlarmEventReportQuery {

//	private List<InstanceStateEnum> states;
	private List<Long> instanceIDes;
	private List<String> metricIDes;
	private List<TimePeriod> timePeriods;
	
	public List<Long> getInstanceIDes() {
		return instanceIDes;
	}
	public void setInstanceIDes(List<Long> instanceIDes) {
		this.instanceIDes = instanceIDes;
	}
	
	public List<String> getMetricIDes() {
		return metricIDes;
	}
	public void setMetricIDes(List<String> metricIDes) {
		this.metricIDes = metricIDes;
	}
//	public List<InstanceStateEnum> getStates() {
//		return states;
//	}
//	public void setStates(List<InstanceStateEnum> states) {
//		this.states = states;
//	}
	public List<TimePeriod> getTimePeriods() {
		return timePeriods;
	}
	public void setTimePeriods(List<TimePeriod> timePeriods) {
		this.timePeriods = timePeriods;
	}
}
