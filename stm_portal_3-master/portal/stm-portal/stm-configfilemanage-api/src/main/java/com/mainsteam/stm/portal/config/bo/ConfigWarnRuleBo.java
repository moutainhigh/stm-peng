package com.mainsteam.stm.portal.config.bo;

import java.io.Serializable;
/**
 * <li>文件名称: ConfigWarnRuleBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月23日
 * @author   caoyong
 */
public class ConfigWarnRuleBo implements Serializable{
	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 2131588513206589287L;
	/**
	 * 告警ID
	 */
	private Long warnId;
	/**
	 * 告警规则接收人ID
	 */
	private Long userId;
	/**
	 * 告警规则接收人名字
	 */
	private String userName;
	/**
	 * 短信（0：已选择；1：未选择；）
	 */
	private Integer message;
	/**
	 * 邮件（0：已选择；1：未选择；）
	 */
	private Integer email;
	/**
	 * Alert（0：已选择；1：未选择；）
	 */
	private Integer alert;
	
	public Long getWarnId() {
		return warnId;
	}
	public void setWarnId(Long warnId) {
		this.warnId = warnId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Integer getMessage() {
		return message;
	}
	public void setMessage(Integer message) {
		this.message = message;
	}
	public Integer getEmail() {
		return email;
	}
	public void setEmail(Integer email) {
		this.email = email;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getAlert() {
		return alert;
	}
	public void setAlert(Integer alert) {
		this.alert = alert;
	}
}
