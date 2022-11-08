package com.mainsteam.stm.system.accesscontrol.bo;

import java.io.Serializable;

/**
 * <li>文件名称: AccessControl.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月5日
 * @author zjf
 */
public class AccessControl implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8131746269071959852L;

	/**
	 * ip过虑开关，默认关闭
	 * */
	private boolean isEnable = false;

	/**
	 * 控制类型，默认为黑名单
	 * */
	private String accessType = "whiteList";

	/**
	 * 白名单列表
	 * */
	private String allow;

	/**
	 * 黑名单列表
	 * */
	private String notAllowed;

	/**
	 * 登录失败次数是否开启
	 */
	private boolean loginFailTimeIsEnable = false;

	/**
	 * 登录失败次
	 */
	private String loginFailTime = "9";

	/**
	 * 登录失败自动解锁是否开启
	 */
	private boolean loginDeblockIsEnable = false;

	/**
	 * 登录失败自动解锁时长
	 */
	private String loginDeblockMinutes = "30";

	/**
	 * 登录密码验证是否开启
	 */
	private boolean loginPassValidityIsEnable = false;

	/**
	 * 登录密码验证时长
	 */
	private String loginPassValidityDays = "90";

	/**
	 * 登录密码验证提醒时长
	 */
	private String loginPassValidityAlertDays = "7";

	public boolean getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}

	public String getAllow() {
		return allow;
	}

	public void setAllow(String allow) {
		this.allow = allow;
	}

	public String getNotAllowed() {
		return notAllowed;
	}

	public void setNotAllowed(String notAllowed) {
		this.notAllowed = notAllowed;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public boolean getLoginFailTimeIsEnable() {
		return loginFailTimeIsEnable;
	}

	public void setLoginFailTimeIsEnable(boolean loginFailTimeIsEnable) {
		this.loginFailTimeIsEnable = loginFailTimeIsEnable;
	}

	public String getLoginFailTime() {
		return loginFailTime;
	}

	public void setLoginFailTime(String loginFailTime) {
		this.loginFailTime = loginFailTime;
	}

	public boolean getLoginDeblockIsEnable() {
		return loginDeblockIsEnable;
	}

	public void setLoginDeblockIsEnable(boolean loginDeblockIsEnable) {
		this.loginDeblockIsEnable = loginDeblockIsEnable;
	}

	public String getLoginDeblockMinutes() {
		return loginDeblockMinutes;
	}

	public void setLoginDeblockMinutes(String loginDeblockMinutes) {
		this.loginDeblockMinutes = loginDeblockMinutes;
	}

	public boolean getLoginPassValidityIsEnable() {
		return loginPassValidityIsEnable;
	}

	public void setLoginPassValidityIsEnable(boolean loginPassValidityIsEnable) {
		this.loginPassValidityIsEnable = loginPassValidityIsEnable;
	}

	public String getLoginPassValidityDays() {
		return loginPassValidityDays;
	}

	public void setLoginPassValidityDays(String loginPassValidityDays) {
		this.loginPassValidityDays = loginPassValidityDays;
	}

	public String getLoginPassValidityAlertDays() {
		return loginPassValidityAlertDays;
	}

	public void setLoginPassValidityAlertDays(String loginPassValidityAlertDays) {
		this.loginPassValidityAlertDays = loginPassValidityAlertDays;
	}

}
