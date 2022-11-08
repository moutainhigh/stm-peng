package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;

public class AlarmRuleVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6581533656600480218L;

	
	private long profileID;
	private String profileType;
	private long alarmRulesID;
	private String userID;
	//批量添加告警规则时用到
	private String[] userIDs;
	private String userName;
	
	//短信,邮件发送是否启用
	private boolean messEnable = false;
	private boolean emailEnable = false;
	private boolean alertEnable = false;
	//告警规则是否含有alarmRuleCondition
	private boolean haveMess = false;
	private boolean haveEmail = false;
	private boolean haveAlert = false;
	//该告警规则是否可被操作
	private boolean operation = true;
	//启用时是否提示设置告警规则
	private boolean messEnableSetContent = false;
	private boolean emailEnableSetContent = false;
	private boolean alertEnableSetContent = false;
	
	public boolean isMessEnableSetContent() {
		return messEnableSetContent;
	}
	public void setMessEnableSetContent(boolean messEnableSetContent) {
		this.messEnableSetContent = messEnableSetContent;
	}
	public boolean isEmailEnableSetContent() {
		return emailEnableSetContent;
	}
	public void setEmailEnableSetContent(boolean emailEnableSetContent) {
		this.emailEnableSetContent = emailEnableSetContent;
	}
	public boolean isAlertEnableSetContent() {
		return alertEnableSetContent;
	}
	public void setAlertEnableSetContent(boolean alertEnableSetContent) {
		this.alertEnableSetContent = alertEnableSetContent;
	}
	public boolean isOperation() {
		return operation;
	}
	public void setOperation(boolean operation) {
		this.operation = operation;
	}
	public String getProfileType() {
		return profileType;
	}
	public void setProfileType(String profileType) {
		this.profileType = profileType;
	}
	public boolean isHaveMess() {
		return haveMess;
	}
	public void setHaveMess(boolean haveMess) {
		this.haveMess = haveMess;
	}
	public boolean isHaveEmail() {
		return haveEmail;
	}
	public void setHaveEmail(boolean haveEmail) {
		this.haveEmail = haveEmail;
	}
	public String[] getUserIDs() {
		return userIDs;
	}
	public void setUserIDs(String[] userIDs) {
		this.userIDs = userIDs;
	}
	public long getProfileID() {
		return profileID;
	}
	public void setProfileID(long profileID) {
		this.profileID = profileID;
	}
	public long getAlarmRulesID() {
		return alarmRulesID;
	}
	public void setAlarmRulesID(long alarmRulesID) {
		this.alarmRulesID = alarmRulesID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public boolean isMessEnable() {
		return messEnable;
	}
	public void setMessEnable(boolean messEnable) {
		this.messEnable = messEnable;
	}
	public boolean isEmailEnable() {
		return emailEnable;
	}
	public void setEmailEnable(boolean emailEnable) {
		this.emailEnable = emailEnable;
	}
	public boolean isAlertEnable() {
		return alertEnable;
	}
	public void setAlertEnable(boolean alertEnable) {
		this.alertEnable = alertEnable;
	}
	public boolean isHaveAlert() {
		return haveAlert;
	}
	public void setHaveAlert(boolean haveAlert) {
		this.haveAlert = haveAlert;
	}
}
