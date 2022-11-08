package com.mainsteam.stm.system.itsm.bo;

import java.io.Serializable;

public class ItsmBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -369207471598569955L;

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
	 * 默认URL
	 */
	private String url;
	
	private boolean isOpen;
	
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
}
