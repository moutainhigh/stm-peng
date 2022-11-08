package com.mainsteam.stm.webService.itsm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SendITM",namespace="http://www.mainsteam.com/SendITM")
public class SendITM {
	
	@XmlElement(name = "arg0", required = true)
	String alarmID;
	@XmlElement(name = "arg1", required = true)
	String caseID;
	@XmlElement(name = "arg2", required = true)
	String status;
	
	public String getAlarmID() {
		return alarmID;
	}
	public void setAlarmID(String alarmID) {
		this.alarmID = alarmID;
	}
	public String getCaseID() {
		return caseID;
	}
	public void setCaseID(String caseID) {
		this.caseID = caseID;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
