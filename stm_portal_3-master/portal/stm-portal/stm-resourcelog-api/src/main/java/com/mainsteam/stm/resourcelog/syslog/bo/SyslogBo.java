package com.mainsteam.stm.resourcelog.syslog.bo;

import java.io.Serializable;
import java.util.Date;

/**
 * <li>文件名称: SyslogBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月11日
 * @author   ziwenwen
 */
public class SyslogBo implements Serializable{
	private static final long serialVersionUID = 8133396478449308384L;
	
	/**
	 * 资源ID
	 */
	private Long resourceId;
	/**
	 * 发生时间
	 */
	private Date alertTime;
	
	/**
	 * 部件
	 */
	private String logUnit;
	
	/**
	 * 消息
	 */
	private String msg;
	
	/**
	 * 日志级别
	 */
	private String level;
	
	/**
	 * 关键字
	 */
	private String keyWords;
	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public String getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}

	public Date getAlertTime() {
		return alertTime;
	}

	public void setAlertTime(Date alertTime) {
		this.alertTime = alertTime;
	}

	public String getLogUnit() {
		return logUnit;
	}

	public void setLogUnit(String logUnit) {
		this.logUnit = logUnit;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
}


