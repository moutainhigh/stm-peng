/**
 * 
 */
package com.mainsteam.stm.state.obj;

import java.io.Serializable;
import java.util.Date;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;


/**
 * 指标状态
 * 
 * @author ziw
 *
 */
public class MetricStateData implements Comparable<Object>, Serializable {

	private static final long serialVersionUID = 479686678502946976L;

	private long instanceID;
	
	private String metricID;
	
	private MetricStateEnum state;
	
	private Date collectTime;
	
	private MetricTypeEnum type;

	private Date updateTime;
	
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
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

	public Date getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(Date collectTime) {
		this.collectTime = collectTime;
	}

	public MetricTypeEnum getType() {
		return type;
	}

	public void setType(MetricTypeEnum type) {
		this.type = type;
	}

	@Override
	public int compareTo(Object o) {
		MetricStateData aft = (MetricStateData)o;
		if(this.getState().getStateVal() > aft.getState().getStateVal())
			return 1;
		else if(this.getState().getStateVal() < aft.getState().getStateVal())
			return -1;
		else
			return 0;
	}

	@Override
	public String toString() {
		return "MetricStateData{" +
				"instanceID=" + instanceID +
				", metricID='" + metricID + '\'' +
				", state=" + state +
				", collectTime=" + collectTime +
				", type=" + type +
				'}';
	}
}
