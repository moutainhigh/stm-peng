package com.mainsteam.stm.common.metric.report;

public class AvailableMetricCountData {
	
	private long instanceID;
	private Float availabilityRate;
	private Float notAvailabilityDurationHour;
	private int notAvailabilityNum;
	/**
	 * 全称是Mean Time To Repair，即平均修复时间。是指可修复产品的平均修复时间，就是从出现故障到修复中间的这段时间
	 */
	private Float MTTR;
	/**
	 * 全称是Mean Time Between Failure，即平均无故障工作时间。就是从新的产品在规定的工作环境条件下开始工作到出现第一个故障的时间的平均值
	 */
	private Float MTBF;
	public long getInstanceID() {
		return instanceID;
	}
	public void setInstanceID(long instanceID) {
		this.instanceID = instanceID;
	}
	public Float getAvailabilityRate() {
		return availabilityRate;
	}
	public void setAvailabilityRate(Float availabilityRate) {
		this.availabilityRate = availabilityRate;
	}
	public Float getNotAvailabilityDurationHour() {
		return notAvailabilityDurationHour;
	}
	public void setNotAvailabilityDurationHour(Float notAvailabilityDurationHour) {
		this.notAvailabilityDurationHour = notAvailabilityDurationHour;
	}
	public int getNotAvailabilityNum() {
		return notAvailabilityNum;
	}
	public void setNotAvailabilityNum(int notAvailabilityNum) {
		this.notAvailabilityNum = notAvailabilityNum;
	}
	public Float getMTTR() {
		return MTTR;
	}
	public void setMTTR(Float mTTR) {
		MTTR = mTTR;
	}
	public Float getMTBF() {
		return MTBF;
	}
	public void setMTBF(Float mTBF) {
		MTBF = mTBF;
	}
	
}
