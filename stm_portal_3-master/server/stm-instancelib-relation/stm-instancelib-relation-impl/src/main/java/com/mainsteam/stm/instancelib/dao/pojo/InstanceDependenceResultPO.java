package com.mainsteam.stm.instancelib.dao.pojo;

public class InstanceDependenceResultPO {

	private long alarmEventId;
	
	private String resultType;
	
	private String resultValue;

	public long getAlarmEventId() {
		return alarmEventId;
	}

	public String getResultType() {
		return resultType;
	}

	public String getResultValue() {
		return resultValue;
	}

	public void setAlarmEventId(long alarmEventId) {
		this.alarmEventId = alarmEventId;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public void setResultValue(String resultValue) {
		this.resultValue = resultValue;
	}
	
}
