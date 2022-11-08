package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;
import java.util.Map;

/**
 * <li>文件名称: StrategyVo.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月
 * @author xhf
 */
 public class StrategyVo implements Serializable{

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = -4519406318272347893L;
	
	/**
	 * 默认策略ID
	 */
	private Long id;
	
	/**
	 * 策略名称
	 */
	private String strategyName;
	
	/**
	 * 策略类型
	 */
	private String strategyType;
	
	/**
	 * 策略备注
	 */
	private String strategyDesc;

	
	private String prentCapacityName;
	
	private String capacityName;
	
	private String resourceId;
	private String resourceName;
	
	private String childProfileIds;
	
	private Map<String, Object> strategyIps;
	
	private Long createUser;
	
	private String updateUser;
	
	private Long domainId;
	
	private String domainName;
	
	private String isUse;
	
	
	public String getIsUse() {
		return isUse;
	}

	public void setIsUse(String isUse) {
		this.isUse = isUse;
	}

	public Long getCreateUser() {
		return createUser;
	}

	public void setCreateUser(Long createUser) {
		this.createUser = createUser;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getDomainName() {
		return domainName;
	}
	
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public Map<String, Object> getStrategyIps() {
		return strategyIps;
	}

	public void setStrategyIps(Map<String, Object> strategyIps) {
		this.strategyIps = strategyIps;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getChildProfileIds() {
		return childProfileIds;
	}

	public void setChildProfileIds(String childProfileIds) {
		this.childProfileIds = childProfileIds;
	}

	public String getPrentCapacityName() {
		return prentCapacityName;
	}

	public void setPrentCapacityName(String prentCapacityName) {
		this.prentCapacityName = prentCapacityName;
	}

	public String getCapacityName() {
		return capacityName;
	}

	public void setCapacityName(String capacityName) {
		this.capacityName = capacityName;
	}

	public String getStrategyDesc() {
		return strategyDesc;
	}

	public void setStrategyDesc(String strategyDesc) {
		this.strategyDesc = strategyDesc;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}

	public String getStrategyType() {
		return strategyType;
	}

	public void setStrategyType(String strategyType) {
		this.strategyType = strategyType;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
    
	
}
