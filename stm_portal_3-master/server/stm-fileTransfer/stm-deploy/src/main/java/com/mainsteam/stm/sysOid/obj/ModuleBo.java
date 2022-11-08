package com.mainsteam.stm.sysOid.obj;

import java.io.Serializable;

public class ModuleBo implements Serializable{
	private static final long serialVersionUID = 5230506401169878682L;

	/**
	 * 模型ID
	 */
	private String resourceId;	//资源列ID
	
	/** 
	 * 厂商名称 
	 */
	private String vendorName;

	private String sysOid;
	
	/** 型号 */
	private String modelNumber;
	/**
	 * 资源类型
	 */
	private String resourceType;
	/**
	 * 是否是系统默认的模型配置
	 */
	private boolean isSystem;
	

	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public String getSysOid() {
		return sysOid;
	}
	public void setSysOid(String sysOid) {
		this.sysOid = sysOid;
	}
	public String getModelNumber() {
		return modelNumber;
	}
	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public boolean isSystem() {
		return isSystem;
	}
	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
}

