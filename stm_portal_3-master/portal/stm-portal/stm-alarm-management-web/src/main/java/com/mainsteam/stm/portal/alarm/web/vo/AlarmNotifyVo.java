package com.mainsteam.stm.portal.alarm.web.vo;

import java.io.Serializable;
import java.util.Date;
/**
 * <li>文件名称: AlarmNotifyVo.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年10月
 * @author xhf
 */
public class AlarmNotifyVo implements Serializable{

	/**
	 * 版本序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 状态
	 */
	private String notifyStatus;
	
	/**
	 * 接收人
	 */
	private String userName;
	
	/**
	 * 接收方式
	 */
	private String  notifytType;
	
	/**
	 * 接收地址
	 */
	private String  notifyAdrr;
	
	/**
	 * 短信发送时间
	 */
	private Date  sendTime;

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public String getNotifyStatus() {
		return notifyStatus;
	}

	public void setNotifyStatus(String notifyStatus) {
		this.notifyStatus = notifyStatus;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNotifytType() {
		return notifytType;
	}

	public void setNotifytType(String notifytType) {
		this.notifytType = notifytType;
	}

	public String getNotifyAdrr() {
		return notifyAdrr;
	}

	public void setNotifyAdrr(String notifyAdrr) {
		this.notifyAdrr = notifyAdrr;
	}
	
	
}
