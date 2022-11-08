package com.mainsteam.stm.portal.threed.web.vo;

import java.io.Serializable;

public class UrlVo implements Serializable{
	private static final long serialVersionUID = 7543827618552375858L;
	private String ip;
	private int port;
	/**
	 * 主页地址
	 */
	private String homePath;
	/**
	 * 后台地址
	 */
	private String adminPath;
	/**
	 * 图片访问路径
	 */
	private String picturePath;
	/**
	 * webservicePath
	 */
	private String webservicePath;
	/**
	 * 集成状态
	 */
	private boolean status;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getHomePath() {
		return homePath;
	}
	public void setHomePath(String homePath) {
		this.homePath = homePath;
	}
	public String getAdminPath() {
		return adminPath;
	}
	public void setAdminPath(String adminPath) {
		this.adminPath = adminPath;
	}
	public String getPicturePath() {
		return picturePath;
	}
	public void setPicturePath(String picturePath) {
		this.picturePath = picturePath;
	}
	public String getWebservicePath() {
		return webservicePath;
	}
	public void setWebservicePath(String webservicePath) {
		this.webservicePath = webservicePath;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

}
