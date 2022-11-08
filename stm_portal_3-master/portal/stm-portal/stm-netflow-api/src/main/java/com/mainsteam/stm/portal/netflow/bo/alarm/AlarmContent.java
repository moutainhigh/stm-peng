package com.mainsteam.stm.portal.netflow.bo.alarm;

import java.util.Date;

public class AlarmContent {
	private String id;
	private String  state; //STATE状态标识，1：已执行动作（action发邮件等）2：已清除，	3：已确认
	private String  thresholdId; //THRESHOLD_ID;告警ID
	private String  serverName; //SERVER_NAME;告警主机
	private String  moduleName; //MODULE_NAME;告警进程
	private Date  createTime; //CREATE_TIME;告警产生时间
	private String  actTime; //ACT_TIME;执行动作时间
	private String  clearTime; //CLEAR_TIME;清除时间
	private String  confirmTime; //CONFIRM_TIME;确认时间
	private String  confirmUser; //CONFIRM_USER;确认人员
	private String  alarmDetail; //ALARM_DETAIL;告警产生时的明细
	private String  clearDetail; //CLEAR_DETAIL;告警清除时的明细
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getThresholdId() {
		return thresholdId;
	}
	public void setThresholdId(String thresholdId) {
		this.thresholdId = thresholdId;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getActTime() {
		return actTime;
	}
	public void setActTime(String actTime) {
		this.actTime = actTime;
	}
	public String getClearTime() {
		return clearTime;
	}
	public void setClearTime(String clearTime) {
		this.clearTime = clearTime;
	}
	public String getConfirmTime() {
		return confirmTime;
	}
	public void setConfirmTime(String confirmTime) {
		this.confirmTime = confirmTime;
	}
	public String getConfirmUser() {
		return confirmUser;
	}
	public void setConfirmUser(String confirmUser) {
		this.confirmUser = confirmUser;
	}
	public String getAlarmDetail() {
		return alarmDetail;
	}
	public void setAlarmDetail(String alarmDetail) {
		this.alarmDetail = alarmDetail;
	}
	public String getClearDetail() {
		return clearDetail;
	}
	public void setClearDetail(String clearDetail) {
		this.clearDetail = clearDetail;
	}
	
}
