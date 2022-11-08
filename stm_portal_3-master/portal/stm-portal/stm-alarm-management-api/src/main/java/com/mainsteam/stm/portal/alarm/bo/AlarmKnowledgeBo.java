package com.mainsteam.stm.portal.alarm.bo;

import java.io.Serializable;

public class AlarmKnowledgeBo implements Serializable{

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 知识id
	 */
	private long id;
	
	/**
	 * 知识分类名称
	 */
	private String alarmKnowledgeName;
	
	/**
	 * 知识内容
	 */
	private String alarmKnowleContent;
	/**
	 * 解决方案Id
	 */
	private long schemeId;
	
	/**
	 * 解决方案名称
	 */
	private String schemeName;
 
	public String getAlarmKnowledgeName() {
		return alarmKnowledgeName;
	}

	public void setAlarmKnowledgeName(String alarmKnowledgeName) {
		this.alarmKnowledgeName = alarmKnowledgeName;
	}

	public String getAlarmKnowleContent() {
		return alarmKnowleContent;
	}

	public void setAlarmKnowleContent(String alarmKnowleContent) {
		this.alarmKnowleContent = alarmKnowleContent;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public long getSchemeId() {
		return schemeId;
	}

	public void setSchemeId(long schemeId) {
		this.schemeId = schemeId;
	}

	public String getSchemeName() {
		return schemeName;
	}

	public void setSchemeName(String schemeName) {
		this.schemeName = schemeName;
	}
  
	
}
