package com.mainsteam.stm.system.ssoforthird.bo;

import java.io.Serializable;

public class SSOForThirdBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 318479120524293174L;
	/**
	 * ip地址
	 */
	private String ip;
	
	/**
	 * 端口号
	 */
	private int port;
	
	/**
	 * 登陆的用户名
	 */
	private String userName;
	/**
	 * ID
	 */
	private long id;
	
	/**
	 * 第三方系统名字
	 */
	private String name;
	
	/**
	 * URL
	 */
	private String wsdlURL;
	
	/**
	 * 说明
	 */
	private String describle;
	
	/**
	 * 状态
	 */
	private String state;
	
	/**
	 * 是否开启0-关闭 1-开启
	 */
	private int isOpen;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWsdlURL() {
		return wsdlURL;
	}

	public void setWsdlURL(String wsdlURL) {
		this.wsdlURL = wsdlURL;
	}

	public String getDescrible() {
		return describle;
	}

	public void setDescrible(String describle) {
		this.describle = describle;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(int isOpen) {
		this.isOpen = isOpen;
	}
	

}
