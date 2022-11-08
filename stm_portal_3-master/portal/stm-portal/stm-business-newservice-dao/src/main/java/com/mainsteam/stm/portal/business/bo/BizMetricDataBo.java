package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;

public class BizMetricDataBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3690039809801379161L;

	private long bizId;
	
	private String bizName;
	
	//业务可用率
	private String availableRate;
	
	//平均故障恢复时间
	private String MTTR;
	
	//平均连续运行时间
	private String MTBF;
	
	//宕机次数
	private String outageTimes;
	
	//宕机时长
	private String downTime;
	
	//响应速度
	private String responseTime;
	
	//访问量
	private String requestTime;
	
	//业务告警次数
	private int alarmCount;
	
	//业务健康度
	private int healthScore;
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	private int status;

	public int getHealthScore() {
		return healthScore;
	}

	public void setHealthScore(int healthScore) {
		this.healthScore = healthScore;
	}

	public int getAlarmCount() {
		return alarmCount;
	}

	public void setAlarmCount(int alarmCount) {
		this.alarmCount = alarmCount;
	}

	public long getBizId() {
		return bizId;
	}

	public void setBizId(long bizId) {
		this.bizId = bizId;
	}

	public String getBizName() {
		return bizName;
	}

	public void setBizName(String bizName) {
		this.bizName = bizName;
	}

	public String getAvailableRate() {
		return availableRate;
	}

	public void setAvailableRate(String availableRate) {
		this.availableRate = availableRate;
	}

	public String getMTTR() {
		return MTTR;
	}

	public void setMTTR(String mTTR) {
		MTTR = mTTR;
	}

	public String getMTBF() {
		return MTBF;
	}

	public void setMTBF(String mTBF) {
		MTBF = mTBF;
	}

	public String getOutageTimes() {
		return outageTimes;
	}

	public void setOutageTimes(String outageTimes) {
		this.outageTimes = outageTimes;
	}

	public String getDownTime() {
		return downTime;
	}

	public void setDownTime(String downTime) {
		this.downTime = downTime;
	}

	public String getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}

	public String getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

}
