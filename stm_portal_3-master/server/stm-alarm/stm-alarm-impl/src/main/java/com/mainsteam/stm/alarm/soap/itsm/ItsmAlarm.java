package com.mainsteam.stm.alarm.soap.itsm;

public class ItsmAlarm {
	private String alarmId;
	private String alarmTrackId;
	private String occurTime;
	private int severity;
	private String message;
	private String source;
	private int   alarmType;
	private String moId;
	private String moType;
	private String additionalInfo;
	private long domainId;
	private String domainName;
	private String recoveryAlarmID;

	
	
	public String getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}
	public String getAlarmTrackId() {
		return alarmTrackId;
	}
	public void setAlarmTrackId(String alarmTrackId) {
		this.alarmTrackId = alarmTrackId;
	}
	public String getOccurTime() {
		return occurTime;
	}
	public void setOccurTime(String occurTime) {
		this.occurTime = occurTime;
	}
	public int getSeverity() {
		return severity;
	}
	public void setSeverity(int severity) {
		this.severity = severity;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public int getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(int alarmType) {
		this.alarmType = alarmType;
	}
	public String getMoId() {
		return moId;
	}
	public void setMoId(String moId) {
		this.moId = moId;
	}
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public void setDomainId(long domainId) {
		this.domainId=domainId;
	}
	public long getDomainId() {
		return this.domainId;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getMoType() {
		return moType;
	}
	public void setMoType(String moType) {
		this.moType = moType;
	}

	public String getRecoveryAlarmID() {
		return recoveryAlarmID;
	}

	public void setRecoveryAlarmID(String recoveryAlarmID) {
		this.recoveryAlarmID = recoveryAlarmID;
	}

	@Override
	public String toString() {
		return "ItsmAlarm{" +
				"alarmId='" + alarmId + '\'' +
				", alarmTrackId='" + alarmTrackId + '\'' +
				", occurTime='" + occurTime + '\'' +
				", severity=" + severity +
				", message='" + message + '\'' +
				", source='" + source + '\'' +
				", alarmType=" + alarmType +
				", moId='" + moId + '\'' +
				", moType='" + moType + '\'' +
				", additionalInfo='" + additionalInfo + '\'' +
				", domainId=" + domainId +
				", domainName='" + domainName + '\'' +
				", recoveryAlarmID=" + recoveryAlarmID +
				'}';
	}
}
