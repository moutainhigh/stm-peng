package com.mainsteam.stm.portal.business.uitl;

public final class BizAlarmParameterDefine {

	public static final String BIZ_NAME = "业务系统名称";
	
	public static final String BIZ_HEALTH = "健康度";
	
	public static final String ALARM_NODE_NAME = "告警节点名称";
	
	public static final String ALARM_NODE_TYPE = "告警节点类型";
	
	public static final String ALARM_NODE_CONTENT = "节点告警内容";
	
	public static final String BIZ_MANAGER = "责任人";
	
	public static final String BIZ_ALARM_LEVEL = "告警级别";
	
	private String bizName;
	
	private String bizHealth;
	
	private String alarmNodeName;
	
	private String alarmNodeType;
	
	private String alarmNodeContent;
	
	private String bizManager;
	
	private String bizAlarmLevel;

	public String getBizName() {
		return bizName;
	}

	public void setBizName(String bizName) {
		this.bizName = bizName;
	}

	public String getBizHealth() {
		return bizHealth;
	}

	public void setBizHealth(String bizHealth) {
		this.bizHealth = bizHealth;
	}

	public String getAlarmNodeName() {
		return alarmNodeName;
	}

	public void setAlarmNodeName(String alarmNodeName) {
		this.alarmNodeName = alarmNodeName;
	}

	public String getAlarmNodeType() {
		return alarmNodeType;
	}

	public void setAlarmNodeType(String alarmNodeType) {
		this.alarmNodeType = alarmNodeType;
	}

	public String getAlarmNodeContent() {
		return alarmNodeContent;
	}

	public void setAlarmNodeContent(String alarmNodeContent) {
		this.alarmNodeContent = alarmNodeContent;
	}

	public String getBizManager() {
		return bizManager;
	}

	public void setBizManager(String bizManager) {
		this.bizManager = bizManager;
	}

	public String getBizAlarmLevel() {
		return bizAlarmLevel;
	}

	public void setBizAlarmLevel(String bizAlarmLevel) {
		this.bizAlarmLevel = bizAlarmLevel;
	}
	
	
}
