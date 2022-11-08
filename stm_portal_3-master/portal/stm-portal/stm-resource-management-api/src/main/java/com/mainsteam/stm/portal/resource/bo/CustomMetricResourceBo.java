package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;

public class CustomMetricResourceBo  implements Serializable{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4772691070535495227L;
	
	private String metricId;
	private long id;
	private String resourceName;
	private String resourceIP;
	private String categoryId;
	private String categoryName;
	/**
	 * 监控类型
	 */
	private String monitorType;
	private long domainId;
	private String domainName;
	
	
	public String getMetricId() {
		return metricId;
	}
	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getResourceIP() {
		return resourceIP;
	}
	public void setResourceIP(String resourceIP) {
		this.resourceIP = resourceIP;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public long getDomainId() {
		return domainId;
	}
	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	public String getMonitorType() {
		return monitorType;
	}
	public void setMonitorType(String monitorType) {
		this.monitorType = monitorType;
	}
	@Override
	public boolean equals(Object obj) {
		CustomMetricResourceBo objBo=(CustomMetricResourceBo)obj;
		return objBo.getId()==this.getId();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}
	
}
