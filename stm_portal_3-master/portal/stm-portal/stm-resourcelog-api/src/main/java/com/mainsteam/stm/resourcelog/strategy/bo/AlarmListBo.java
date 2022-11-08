package com.mainsteam.stm.resourcelog.strategy.bo;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * <li>文件名称: AlarmListBo</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月26日 上午10:17:16
 * @author   xiaolei
 */
public class AlarmListBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5234498472940030197L;
	
	private String id;
	/**
	 * 发生时间
	 */
	private Date occurredTime;
	
	/**
	 * 级别
	 */
	private String level;
	
	/**
	 * 部件
	 */
	private String component;
	
	/**
	 * 消息内容
	 */
	private String msgContent;
	
	/**
	 * 关键字
	 */
	private String keyWords;
	
	/**
	 * snmptrap级别
	 */
	private String snmptrapType;
	
	private String startTime;
	
	private String endTime;
	public Date getOccurredTime() {
		return occurredTime;
	}

	public void setOccurredTime(Date occurredTime) {
		this.occurredTime = occurredTime;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public String getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}

	public String getSnmptrapType() {
		return snmptrapType;
	}

	public void setSnmptrapType(String snmptrapType) {
		this.snmptrapType = snmptrapType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
