package com.mainsteam.stm.resourcelog.syslog.bo;

import java.io.Serializable;

/**
 * <li>文件名称: StrategyRuleBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 策略规则对象</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月5日
 * @author   ziwenwen
 */
public class SysLogRuleBo implements Serializable{
	private static final long serialVersionUID = 2717733374809833841L;

	/**
	 * 策略id
	 */
	Long strategyId;
	
	/**
	 * 规则id
	 */
	Long id;
	
	/**
	 * 规则名称
	 */
	String name;
	
	/**
	 * 关键字
	 */
	String keywords;
	
	/**
	 * 逻辑类型：暂定0为或 1为与的关系
	 */
	int logicType;
	
	/**
	 * 日志级别
	 */
//	int[] logLevel;
	String logLevel;
	
	/**
	 * 是否触发告警 1触发 0不触发
	 */
	int isAlarm;
	
	/**
	 * 映射成oc系统的日志级别
	 */
//	int ocLevel;
	String alarmLevel;
	
	/**
	 * 是否开启 1表示开启 0表示关闭
	 */
	int isOpen;
	
	/**
	 * 描述 备注
	 */
	String description;

	public Long getStrategyId() {
		return strategyId;
	}

	public void setStrategyId(Long strategyId) {
		this.strategyId = strategyId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public int getLogicType() {
		return logicType;
	}

	public void setLogicType(int logicType) {
		this.logicType = logicType;
	}

	/**
	 * @return the logLevel
	 */
	public String getLogLevel() {
		return logLevel;
	}

	/**
	 * @param logLevel the logLevel to set
	 */
	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}
	
	/**
	 * @return the isAlarm
	 */
	public int getIsAlarm() {
		return isAlarm;
	}

	/**
	 * @param isAlarm the isAlarm to set
	 */
	public void setIsAlarm(int isAlarm) {
		this.isAlarm = isAlarm;
	}

	public String getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(String alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	/**
	 * @return the isOpen
	 */
	public int getIsOpen() {
		return isOpen;
	}

	/**
	 * @param isOpen the isOpen to set
	 */
	public void setIsOpen(int isOpen) {
		this.isOpen = isOpen;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}


