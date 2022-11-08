package com.mainsteam.stm.state.calculate;

import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.state.obj.MetricStateData;

@Deprecated
public class PerformanceStateCaluteResult {
	private boolean alarmNotifiable;
	private MetricStateData metricStateData;
	private ProfileThreshold threshhold;
	private int flapping;
	
	public PerformanceStateCaluteResult(){
	}
	
	public PerformanceStateCaluteResult(boolean alarmNotifiable){
		this.alarmNotifiable = alarmNotifiable;
	}
	
	public int getFlapping() {
		return flapping;
	}
	public void setFlapping(int flapping) {
		this.flapping = flapping;
	}
	public MetricStateData getMetricStateData() {
		return metricStateData;
	}
	public void setMetricStateData(MetricStateData metricStateData) {
		this.metricStateData = metricStateData;
	}
	public ProfileThreshold getThreshhold() {
		return threshhold;
	}
	public void setThreshhold(ProfileThreshold threshhold) {
		this.threshhold = threshhold;
	}
	public boolean isAlarmNotifiable() {
		return alarmNotifiable;
	}
	public void setAlarmNotifiable(boolean alarmNotifiable) {
		this.alarmNotifiable = alarmNotifiable;
	}
}
