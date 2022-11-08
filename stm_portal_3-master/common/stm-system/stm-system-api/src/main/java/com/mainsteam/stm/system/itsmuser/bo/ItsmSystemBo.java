package com.mainsteam.stm.system.itsmuser.bo;

import java.io.Serializable;

public class ItsmSystemBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2676878650270607355L;
	
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
