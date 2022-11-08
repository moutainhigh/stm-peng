package com.mainsteam.stm.state.obj;

import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.profilelib.obj.Threshold;

public class MetricStateChangeData {
	private MetricStateData newState;
	private MetricStateData oldState;
	private ResourceMetricDef metricDef;
	private int changeNum;
	private MetricCalculateData metricData;
	private Threshold threshhold;
	private boolean causeBySelf;
	/**是否需要通知*/
	private boolean notifiable;
	
	public MetricStateData getNewState() {
		return newState;
	}
	public void setNewState(MetricStateData newState) {
		this.newState = newState;
	}
	public MetricStateData getOldState() {
		return oldState;
	}
	public void setOldState(MetricStateData oldState) {
		this.oldState = oldState;
	}
	public int getChangeNum() {
		return changeNum;
	}
	public void setChangeNum(int changeNum) {
		this.changeNum = changeNum;
	}
	public ResourceMetricDef getMetricDef() {
		return metricDef;
	}
	public void setMetricDef(ResourceMetricDef metricDef) {
		this.metricDef = metricDef;
	}
	public MetricCalculateData getMetricData() {
		return metricData;
	}
	public void setMetricData(MetricCalculateData metricData) {
		this.metricData = metricData;
	}
	public Threshold getThreshhold() {
		return threshhold;
	}
	public void setThreshhold(Threshold threshhold) {
		this.threshhold = threshhold;
	}
	public boolean isCauseBySelf() {
		return causeBySelf;
	}
	public void setCauseBySelf(boolean causeBySelf) {
		this.causeBySelf = causeBySelf;
	}
	public void setNotifiable(boolean b) {
		this.notifiable=b;
	}
	public boolean isNotifiable() {
		return this.notifiable;
	}
}
