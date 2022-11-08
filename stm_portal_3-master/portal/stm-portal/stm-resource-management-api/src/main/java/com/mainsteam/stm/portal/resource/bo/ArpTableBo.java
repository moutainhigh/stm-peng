package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;

public class ArpTableBo implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	// 资源实例ID
	private long resourceId;

	private String ifIndex;
	private String macAddress;
	private String iPAddress;
	private String arpType;
	
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	public String getIfIndex() {
		return ifIndex;
	}
	public void setIfIndex(String ifIndex) {
		this.ifIndex = ifIndex;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public String getiPAddress() {
		return iPAddress;
	}
	public void setiPAddress(String iPAddress) {
		this.iPAddress = iPAddress;
	}
	public String getArpType() {
		return arpType;
	}
	public void setArpType(String arpType) {
		this.arpType = arpType;
	}
	
	
}
