package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;


public class NetstatBo implements Serializable{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8368521137543116151L;
	
	private String foreignAddress;
	private String localAddress;
	private String protocol;
	private String state;
	
	public String getForeignAddress() {
		return foreignAddress;
	}
	public void setForeignAddress(String foreignAddress) {
		this.foreignAddress = foreignAddress;
	}
	public String getLocalAddress() {
		return localAddress;
	}
	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
}
