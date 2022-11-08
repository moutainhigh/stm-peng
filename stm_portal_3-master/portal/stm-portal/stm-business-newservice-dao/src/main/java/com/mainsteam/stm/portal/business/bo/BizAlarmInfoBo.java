package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;

public class BizAlarmInfoBo implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -5879675787642006597L;

	//id
	private long id;
	
	//业务ID
	private long bizId;
	
	//致命状态符号
	private String deathExpression;
	
	//致命状态阈值
	private String deathThreshold;
	
	//致命状态告警内容
	private String deathAlarmContent;
	
	//严重状态符号
	private String seriousExpression;
	
	//严重状态阈值
	private String seriousThreshold;
	
	//严重状态告警内容
	private String seriousAlarmContent;
	
	//警告状态符号
	private String warnExpression;
	
	//警告状态阈值
	private String warnThreshold;
	
	//警告状态告警内容
	private String warnAlarmContent;
	
	//恢复正常告警内容
	private String normalContent;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getBizId() {
		return bizId;
	}

	public void setBizId(long bizId) {
		this.bizId = bizId;
	}

	public String getDeathExpression() {
		return deathExpression;
	}

	public void setDeathExpression(String deathExpression) {
		this.deathExpression = deathExpression;
	}

	public String getDeathThreshold() {
		return deathThreshold;
	}

	public void setDeathThreshold(String deathThreshold) {
		this.deathThreshold = deathThreshold;
	}

	public String getDeathAlarmContent() {
		return deathAlarmContent;
	}

	public void setDeathAlarmContent(String deathAlarmContent) {
		this.deathAlarmContent = deathAlarmContent;
	}

	public String getSeriousExpression() {
		return seriousExpression;
	}

	public void setSeriousExpression(String seriousExpression) {
		this.seriousExpression = seriousExpression;
	}

	public String getSeriousThreshold() {
		return seriousThreshold;
	}

	public void setSeriousThreshold(String seriousThreshold) {
		this.seriousThreshold = seriousThreshold;
	}

	public String getSeriousAlarmContent() {
		return seriousAlarmContent;
	}

	public void setSeriousAlarmContent(String seriousAlarmContent) {
		this.seriousAlarmContent = seriousAlarmContent;
	}

	public String getWarnExpression() {
		return warnExpression;
	}

	public void setWarnExpression(String warnExpression) {
		this.warnExpression = warnExpression;
	}

	public String getWarnThreshold() {
		return warnThreshold;
	}

	public void setWarnThreshold(String warnThreshold) {
		this.warnThreshold = warnThreshold;
	}

	public String getWarnAlarmContent() {
		return warnAlarmContent;
	}

	public void setWarnAlarmContent(String warnAlarmContent) {
		this.warnAlarmContent = warnAlarmContent;
	}

	public String getNormalContent() {
		return normalContent;
	}

	public void setNormalContent(String normalContent) {
		this.normalContent = normalContent;
	}
	
}
