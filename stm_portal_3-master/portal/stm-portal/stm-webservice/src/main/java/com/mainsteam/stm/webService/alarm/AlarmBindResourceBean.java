package com.mainsteam.stm.webService.alarm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Calendar;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "AlarmBindResourceBean")
public class AlarmBindResourceBean {

	@XmlElement(name = "alarmID", required = true)
	private String alarmID;

	@XmlElement(name = "alarmLevel", required = true)
	private AlarmStateEnum alarmLevel;

	@XmlElement(name = "alarmContent", required = true)
	private String alarmContent;

	@XmlElement(name = "AlarmCreateTime", required = true)
	private Calendar alarmCreateTime;

	@XmlElement(name = "isRecovered")
	private boolean isRecovered;

	@XmlElement(name = "resourceType", required = true)
	private ResourceTypeEnum resourceType;

	@XmlElement(name = "deviceType")
	private DeviceTypeEnum deviceType;

	@XmlElement(name = "ip", required = true)
	private String ip;

	@XmlElement(name = "parameter1")
	private String parameter1;

	@XmlElement(name = "parameter2")
	private String parameter2;

	@XmlElement(name = "parameter3")
	private String parameter3;

	@XmlElement(name = "parameter4")
	private String parameter4;

	@XmlElement(name = "instanceID")
	private String instanceID;

	public String getAlarmID() {
		return alarmID;
	}

	public void setAlarmID(String alarmID) {
		this.alarmID = alarmID;
	}

	public AlarmStateEnum getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(AlarmStateEnum alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	public String getAlarmContent() {
		return alarmContent;
	}

	public void setAlarmContent(String alarmContent) {
		this.alarmContent = alarmContent;
	}

	public Calendar getAlarmCreateTime() {
		return alarmCreateTime;
	}

	public void setAlarmCreateTime(Calendar alarmCreateTime) {
		this.alarmCreateTime = alarmCreateTime;
	}

	public boolean isRecovered() {
		return isRecovered;
	}

	public void setRecovered(boolean recovered) {
		isRecovered = recovered;
	}

	public ResourceTypeEnum getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceTypeEnum resourceType) {
		this.resourceType = resourceType;
	}

	public DeviceTypeEnum getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceTypeEnum deviceType) {
		this.deviceType = deviceType;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getParameter1() {
		return parameter1;
	}

	public void setParameter1(String parameter1) {
		this.parameter1 = parameter1;
	}

	public String getParameter2() {
		return parameter2;
	}

	public void setParameter2(String parameter2) {
		this.parameter2 = parameter2;
	}

	public String getParameter3() {
		return parameter3;
	}

	public void setParameter3(String parameter3) {
		this.parameter3 = parameter3;
	}

	public String getParameter4() {
		return parameter4;
	}

	public void setParameter4(String parameter4) {
		this.parameter4 = parameter4;
	}

	public String getInstanceID() {
		return instanceID;
	}

	public void setInstanceID(String instanceID) {
		this.instanceID = instanceID;
	}

	@Override
	public String toString() {
		return "AlarmBindResourceBean{" +
				"alarmID='" + alarmID + '\'' +
				", alarmLevel=" + alarmLevel +
				", alarmContent='" + alarmContent + '\'' +
				", alarmCreateTime=" + alarmCreateTime +
				", isRecovered=" + isRecovered +
				", resourceType=" + resourceType +
				", deviceType=" + deviceType +
				", ip='" + ip + '\'' +
				", parameter1='" + parameter1 + '\'' +
				", parameter2='" + parameter2 + '\'' +
				", parameter3='" + parameter3 + '\'' +
				", parameter4='" + parameter4 + '\'' +
				'}';
	}
}
