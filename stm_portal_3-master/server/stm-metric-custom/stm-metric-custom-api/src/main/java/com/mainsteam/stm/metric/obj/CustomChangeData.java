package com.mainsteam.stm.metric.obj;

import java.io.Serializable;
import java.util.List;

public class CustomChangeData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6216883177127062944L;
	private String metricId;
	private Boolean isAlarm;
	private Boolean isMonitor;
	private List<Long> cancelInstanceIds;
	
	public String getMetricId() {
		return metricId;
	}
	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}
	public Boolean getIsAlarm() {
		return isAlarm;
	}
	public Boolean getIsMonitor() {
		return isMonitor;
	}
	
	public void setIsAlarm(Boolean isAlarm) {
		this.isAlarm = isAlarm;
	}
	public void setIsMonitor(Boolean isMonitor) {
		this.isMonitor = isMonitor;
	}
	public List<Long> getCancelInstanceIds() {
		return cancelInstanceIds;
	}
	public void setCancelInstanceIds(List<Long> cancelInstanceIds) {
		this.cancelInstanceIds = cancelInstanceIds;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(100);
		b.append("CustomMetricChangeData metricId=").append(metricId);
		b.append(", isAlarm=").append(isAlarm);
		b.append(", isMonitor=").append(isMonitor);
		b.append(", cancelInstanceIds=").append(cancelInstanceIds).append("]");
		return b.toString();
	}
}
