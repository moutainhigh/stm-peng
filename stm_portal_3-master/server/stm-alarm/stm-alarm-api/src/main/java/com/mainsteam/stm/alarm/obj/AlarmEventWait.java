package com.mainsteam.stm.alarm.obj;

import java.util.Date;

public class AlarmEventWait {

	private long id;
	private long eventID;
	private long ruleID;
	private String content;
	private String recoverKey;
	private Date execTime;


	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getExecTime() {
		return execTime;
	}
	public void setExecTime(Date execTime) {
		this.execTime = execTime;
	}
	public String getRecoverKey() {
		return recoverKey;
	}
	public void setRecoverKey(String recoverKey) {
		this.recoverKey = recoverKey;
	}
	public long getEventID() {
		return eventID;
	}
	public void setEventID(long eventID) {
		this.eventID = eventID;
	}
	public long getRuleID() {
		return ruleID;
	}
	public void setRuleID(long ruleID) {
		this.ruleID = ruleID;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("AlarmEventWait: [id:").append(getId()).append(";eventID:").append(getEventID()).append(";ruleID:")
			.append(getRuleID()).append(";recoverKey:").append(getRecoverKey()).append(";execTime:").append(getExecTime())
			.append(";content:").append(getContent());
		sb.append("]");
		return sb.toString();
	}
}
