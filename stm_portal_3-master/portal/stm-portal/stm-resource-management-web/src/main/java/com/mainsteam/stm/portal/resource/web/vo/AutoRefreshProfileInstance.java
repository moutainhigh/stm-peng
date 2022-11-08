package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;


/**
 * <li>文件名称: MainResourceStrategyVo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2016年4月29日
 * @author   pengl
 */
public class AutoRefreshProfileInstance implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2196851233983295949L;

	private long instanceId;
	
	private String resourceName;
	
	private String resourceShowName;
	
	private String resourceIp;
	
	private long profileId;
	
	private String profileName;
	
	private long domainId;
	
	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceShowName() {
		return resourceShowName;
	}

	public void setResourceShowName(String resourceShowName) {
		this.resourceShowName = resourceShowName;
	}

	public String getResourceIp() {
		return resourceIp;
	}

	public void setResourceIp(String resourceIp) {
		this.resourceIp = resourceIp;
	}

	public long getProfileId() {
		return profileId;
	}

	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public long getDomainId() {
		return domainId;
	}

	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}
	
	
	
}
