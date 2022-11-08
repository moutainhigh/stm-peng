package com.mainsteam.stm.portal.inspect.bo;

public class ReportResourceInstance{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2810802187720606332L;
	private long id;
	private String showName;
	private String discoverIP;
	private String resourceId;
	private String resourceName;
	
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getShowName() {
		return showName;
	}
	public void setShowName(String showName) {
		this.showName = showName;
	}
	public String getDiscoverIP() {
		return discoverIP;
	}
	public void setDiscoverIP(String discoverIP) {
		this.discoverIP = discoverIP;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	
	
}
