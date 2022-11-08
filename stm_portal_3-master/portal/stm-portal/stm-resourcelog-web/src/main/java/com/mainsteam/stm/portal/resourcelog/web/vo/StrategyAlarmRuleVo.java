package com.mainsteam.stm.portal.resourcelog.web.vo;

import java.io.Serializable;

/**
 * 
 * <li>文件名称: StrategyAlarmRuleVo</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 告警规则前台页面展示VO</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月21日 上午10:07:29
 * @author   xiaolei
 */
public class StrategyAlarmRuleVo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3531839346771854956L;

	/**
	 * 用户ID
	 */
	private Long userId;
	
	/**
	 * 策略ID
	 */
	private Long strategyId;
	/**
	 * 用户姓名
	 */
	private String userName;
	
	private int isMsg;
	
	private int isMail;
	
	//告警规则批量更新发送方式的启用状态时用到
	private String[] enableMessageRulList;
	
	private String[] enableEmailRulList;
	
	private String[] messageRulList;
	
	private String[] emailRulList;
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getIsMsg() {
		return isMsg;
	}

	public void setIsMsg(int isMsg) {
		this.isMsg = isMsg;
	}

	public int getIsMail() {
		return isMail;
	}

	public void setIsMail(int isMail) {
		this.isMail = isMail;
	}

	public Long getStrategyId() {
		return strategyId;
	}

	public void setStrategyId(Long strategyId) {
		this.strategyId = strategyId;
	}

	public String[] getEnableMessageRulList() {
		return enableMessageRulList;
	}

	public void setEnableMessageRulList(String[] enableMessageRulList) {
		this.enableMessageRulList = enableMessageRulList;
	}

	public String[] getEnableEmailRulList() {
		return enableEmailRulList;
	}

	public void setEnableEmailRulList(String[] enableEmailRulList) {
		this.enableEmailRulList = enableEmailRulList;
	}

	public String[] getMessageRulList() {
		return messageRulList;
	}

	public void setMessageRulList(String[] messageRulList) {
		this.messageRulList = messageRulList;
	}

	public String[] getEmailRulList() {
		return emailRulList;
	}

	public void setEmailRulList(String[] emailRulList) {
		this.emailRulList = emailRulList;
	}
}
