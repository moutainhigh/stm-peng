/**
 * 
 */
package com.mainsteam.stm.resourcelog.syslog.bo;

import java.io.Serializable;

/**
 * <li>文件名称: ResourceSearchBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: </li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月13日
 * @author   lil
 */
public class ResourceSearchBo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2131149276869077767L;

	private long strategyId;
	private long resourceId;
	private String resourceIp;
	private String resourceName;
	private String resourceType;
	/**
	 * @return the strategyId
	 */
	public long getStrategyId() {
		return strategyId;
	}
	/**
	 * @param strategyId the strategyId to set
	 */
	public void setStrategyId(long strategyId) {
		this.strategyId = strategyId;
	}
	/**
	 * @return the resourceId
	 */
	public long getResourceId() {
		return resourceId;
	}
	/**
	 * @param resourceId the resourceId to set
	 */
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	/**
	 * @return the resourceIp
	 */
	public String getResourceIp() {
		return resourceIp;
	}
	/**
	 * @param resourceIp the resourceIp to set
	 */
	public void setResourceIp(String resourceIp) {
		this.resourceIp = resourceIp;
	}
	/**
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}
	/**
	 * @param resourceName the resourceName to set
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	/**
	 * @return the resourceType
	 */
	public String getResourceType() {
		return resourceType;
	}
	/**
	 * @param resourceType the resourceType to set
	 */
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
