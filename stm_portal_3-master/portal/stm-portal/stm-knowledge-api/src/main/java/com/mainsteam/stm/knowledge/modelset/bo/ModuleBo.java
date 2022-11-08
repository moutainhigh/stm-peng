package com.mainsteam.stm.knowledge.modelset.bo;

import java.io.Serializable;

/**
 * <li>文件名称: ModuleBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月2日
 * @author   ziwenwen
 */
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


