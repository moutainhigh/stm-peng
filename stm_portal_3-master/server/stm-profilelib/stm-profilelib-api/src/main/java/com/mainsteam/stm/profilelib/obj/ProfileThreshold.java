package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;

import com.mainsteam.stm.caplib.dict.PerfMetricStateEnum;

/**
 * 策略阀值
 * @author xiaoruqiang
 */
public class ProfileThreshold extends Threshold implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7639232926725400862L;
	/**
	 * 策略ID
	 */
	private long profileId;
	/**
	 * 指标值
	 */
	private String metricId;
	/**
	 * 基线
	 */
	private long timelineId;
	/**
	 * 性能指标状态值
	 */
	private PerfMetricStateEnum perfMetricStateEnum;
	

	public ProfileThreshold(){
	}

	public long getProfileId() {
		return profileId;
	}

	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}

	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public PerfMetricStateEnum getPerfMetricStateEnum() {
		return perfMetricStateEnum;
	}

	public void setPerfMetricStateEnum(PerfMetricStateEnum perfMetricStateEnum) {
		this.perfMetricStateEnum = perfMetricStateEnum;
	}


	public long getTimelineId() {
		return timelineId;
	}

	public void setTimelineId(long timelineId) {
		this.timelineId = timelineId;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(500);
		b.append("[");
		b.append(" mkId=").append(getThreshold_mkId());
		b.append(" profileId=").append(getProfileId());
		b.append(" timelineId=").append(getTimelineId());
		b.append(" metricId=").append(getMetricId());
		b.append(" perfMetricStateEnum=").append(getPerfMetricStateEnum().name());
		b.append(" thresholdValue=").append(getThresholdValue());
		b.append(" expressionOperator=").append(getExpressionOperator());
		b.append(" ThresholdExpression=").append(getThresholdExpression());
		b.append("]");
		return b.toString();
	}
}
