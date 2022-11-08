package com.mainsteam.stm.webService.alarm;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "AlarmSyncBean")
public class AlarmSyncBean {

	@XmlElement(name = "sourceID", required = true)
	private String sourceID;

	@XmlElement(name = "sourceName", required = true)
	private String sourceName;

	@XmlElement(name = "sourceIP", required = true)
	private String sourceIP;

	@XmlElement(name = "content", required = true)
	private String content;

	@XmlElement(name = "alarmType", required = true)
	private String alarmType;

	@XmlElement(name = "createTime", required = true)
	private Date createTime;

	@XmlElement(name = "alarmLevel", required = true)
	private AlarmStateEnum alarmLevel;

	public AlarmStateEnum getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(AlarmStateEnum alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	public String getSourceID() {
		return sourceID;
	}

	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getSourceIP() {
		return sourceIP;
	}

	public void setSourceIP(String sourceIP) {
		this.sourceIP = sourceIP;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
