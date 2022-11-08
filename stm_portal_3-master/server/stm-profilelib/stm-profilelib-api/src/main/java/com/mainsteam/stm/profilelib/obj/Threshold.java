package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;
/**
 * 信息指标功能把expressionOperator，thresholdValue合并成thresholdExpression
 * @author Xiao Ruqiang
 * @version 731
 */
public class Threshold implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2319925344362313014L;

	/**
	 * 自动增长主键值
	 */
	private long threshold_mkId;
	/**
	 * 告警内容模板
	 */
	private String alarmTemplate;
	/**
	 * 表达式操作
	 */
	@Deprecated 
	private String expressionOperator;
	/**
	 * 阈值表达式
	 */
	private String thresholdExpression = null;
	
	/**
	 * 阀值
	 */
	@Deprecated 
	private String thresholdValue;
	
	
	public long getThreshold_mkId() {
		return threshold_mkId;
	}

	public void setThreshold_mkId(long threshold_mkId) {
		this.threshold_mkId = threshold_mkId;
	}
	
	@Deprecated 
	public String getExpressionOperator() {
		return expressionOperator;
	}
	
	@Deprecated 
	public String getThresholdValue() {
		return thresholdValue;
	}
	
	@Deprecated 
	public void setExpressionOperator(String expressionOperator) {
		this.expressionOperator = expressionOperator;
	}
	
	@Deprecated
	public void setThresholdValue(String thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	public String getThresholdExpression() {
		return thresholdExpression;
	}

	public void setThresholdExpression(String thresholdExpression) {
		this.thresholdExpression = thresholdExpression;
	}

	public String getAlarmTemplate() {
		return alarmTemplate;
	}

	public void setAlarmTemplate(String alarmTemplate) {
		this.alarmTemplate = alarmTemplate;
	}
	
}
