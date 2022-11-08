package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;

import com.mainsteam.stm.instancelib.obj.Instance;

public class BizResourceInstance extends Instance implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5767638253357933915L;

	private String resourceId;
	
	private String resourceName;
	
	private String discoverIP;
	
	private String showName;
	
	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getDiscoverIP() {
		return discoverIP;
	}

	public void setDiscoverIP(String discoverIP) {
		this.discoverIP = discoverIP;
	}
	
}
