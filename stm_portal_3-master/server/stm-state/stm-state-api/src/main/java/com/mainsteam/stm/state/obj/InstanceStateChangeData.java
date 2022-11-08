package com.mainsteam.stm.state.obj;

import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.dataprocess.MetricCalculateData;

public class InstanceStateChangeData {

	private InstanceStateData newState;
	private InstanceStateData oldState;
	private int changeNum;
	private MetricCalculateData metricData;
	private String causeByMetricID;
	private boolean notifiable;
	private ResourceMetricDef causeByMetricDef;
	
	public String getCauseByMetricID() {
		return causeByMetricID;
	}
	public void setCauseByMetricID(String causeByMetricID) {
		this.causeByMetricID = causeByMetricID;
	}
	public InstanceStateData getNewState() {
		return newState;
	}
	public void setNewState(InstanceStateData newState) {
		this.newState = newState;
	}
	public InstanceStateData getOldState() {
		return oldState;
	}
	public void setOldState(InstanceStateData oldState) {
		this.oldState = oldState;
	}
	public int getChangeNum() {
		return changeNum;
	}
	public void setChangeNum(int changeNum) {
		this.changeNum = changeNum;
	}
	public MetricCalculateData getMetricData() {
		return metricData;
	}
	public void setMetricData(MetricCalculateData metricData) {
		this.metricData = metricData;
	}
	public ResourceMetricDef getCauseByMetricDef() {
		return causeByMetricDef;
	}
	public void setCauseByMetricDef(ResourceMetricDef causeByMetricDef) {
		this.causeByMetricDef = causeByMetricDef;
	}
	public boolean isNotifiable() {
		return notifiable;
	}
	public void setNotifiable(boolean notifiable) {
		this.notifiable = notifiable;
	}
}
