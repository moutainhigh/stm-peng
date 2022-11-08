
package com.mainsteam.stm.state.thirdparty.obj;

import com.mainsteam.stm.common.metric.obj.MetricStateEnum;

import java.io.Serializable;
import java.util.Date;


/**
 * Add by Xiaopf
 * 2016-06-15
 * Chinawiserv.com
 */
public class ThirdPartyMetricStateData implements Comparable, Serializable {

	private static final long serialVersionUID = 479686678502946976L;

	private long instanceID;
	
	private String metricID;
	
	private MetricStateEnum state;
	
	private Date updateTime;
	
	public long getInstanceID() {
		return instanceID;
	}

	public void setInstanceID(long instanceID) {
		this.instanceID = instanceID;
	}

	public String getMetricID() {
		return metricID;
	}

	public void setMetricID(String metricID) {
		this.metricID = metricID;
	}

	public MetricStateEnum getState() {
		return state;
	}

	public void setState(MetricStateEnum state) {
		this.state = state;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public int compareTo(Object o) {
		ThirdPartyMetricStateData aft = (ThirdPartyMetricStateData)o;
		if(this.getState().getStateVal() > aft.getState().getStateVal())
			return 1;
		else if(this.getState().getStateVal() < aft.getState().getStateVal())
			return -1;
		else
			return 0;
	}

	@Override
	public String toString() {
		return "ThirdPartyMetricStateData{" +
				"instanceID=" + instanceID +
				", metricID='" + metricID + '\'' +
				", state=" + state +
				", updateTime=" + updateTime +
				'}';
	}
}
