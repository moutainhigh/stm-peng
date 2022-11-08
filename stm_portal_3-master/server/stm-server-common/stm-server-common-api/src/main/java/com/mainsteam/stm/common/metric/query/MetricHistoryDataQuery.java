package com.mainsteam.stm.common.metric.query;

import java.util.Date;

import com.mainsteam.stm.util.DateUtil;
import com.mainsteam.stm.util.PartitionDateUtil;

public class MetricHistoryDataQuery {
	private String metricID;
	private long instanceID;
	private Date startTime;
	private Date endTime;
	private String startTimeStr;
	private String endTimeStr;
	private int startParttion;
	private int endParttion;
	
	
	public int getStartParttion() {
		return startParttion;
	}
	public int getEndParttion() {
		return endParttion;
	}
	public void setStartParttion(int startParttion) {
		this.startParttion = startParttion;
	}
	public void setEndParttion(int endParttion) {
		this.endParttion = endParttion;
	}
	public String getStartTimeStr() {
		return startTimeStr;
	}
	public String getEndTimeStr() {
		return endTimeStr;
	}
	public String getMetricID() {
		return metricID;
	}
	public void setMetricID(String metricID) {
		this.metricID = metricID;
	}
	public long getInstanceID() {
		return instanceID;
	}
	public void setInstanceID(long instanceID) {
		this.instanceID = instanceID;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
		if(startTime!=null){
			this.startTimeStr = DateUtil.format(startTime, DateUtil.DEFAULT_DATETIME_FORMAT);
			this.startParttion = Integer.parseInt(PartitionDateUtil.partitionNameFormat(startTime));
		}
	}
	
	public Date getEndTime() {
		return endTime;
	}
	
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
		if(endTime!=null){
			this.endTimeStr = DateUtil.format(endTime, DateUtil.DEFAULT_DATETIME_FORMAT);
			this.endParttion = Integer.parseInt(PartitionDateUtil.partitionNameFormat(endTime));
		}
	}
}
