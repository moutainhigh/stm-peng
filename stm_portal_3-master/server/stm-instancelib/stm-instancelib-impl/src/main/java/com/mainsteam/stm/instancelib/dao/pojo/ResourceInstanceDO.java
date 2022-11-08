package com.mainsteam.stm.instancelib.dao.pojo;


public class ResourceInstanceDO {
	/**
	 * 资源ID 
	 */
	private String resourceId;
	/**
	 * 资源分类
	 */
	private String categoryId;

	/**
	 * 发现时使用ip
	 */
	private String showIP;
	/**
	 * 发现时使用节点
	 */
	private String discoverNode;
	
    /**
     * 发现方式（snmp,telnet,ssh方式）
     */
	private String discoverWay;
	/**
	 * 生命周期状态
	 */
	private String lifeState;
	
	  
	
	private Long parentId;
	/**
	 * 实例ID
	 */
	private long instanceId;
	/**
	 * 实例名称
	 */
	private String instanceName;
	/**
	 * 主机，网络等类型
	 */
	private String instanceType;
	
	private long domainId;
	
	private String instanceShowName;
	
	private String isCore;
	
	private String createUserAccount;
	
	public String getInstanceShowName() {
		return instanceShowName;
	}

	public void setInstanceShowName(String instanceShowName) {
		this.instanceShowName = instanceShowName;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}


	public Long getParentId() {
		return parentId;
	}
	
	/**
	 * 
	 * @param parentId 需要包装Long对象
	 */
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getShowIP() {
		return showIP;
	}

	public void setShowIP(String showIP) {
		this.showIP = showIP;
	}

	public String getDiscoverNode() {
		return discoverNode;
	}

	public void setDiscoverNode(String discoverNode) {
		this.discoverNode = discoverNode;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getLifeState() {
		return lifeState;
	}

	public void setLifeState(String lifeState) {
		this.lifeState = lifeState;
	}

	public long getInstanceId() {
		return instanceId;
	}
	/**
	 * 
	 * @param instanceId 需要包装Long对象
	 */
	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}

	public String getDiscoverWay() {
		return discoverWay;
	}

	public void setDiscoverWay(String discoverWay) {
		this.discoverWay = discoverWay;
	}

	public long getDomainId() {
		return domainId;
	}

	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}

	public String getIsCore() {
		return isCore;
	}

	public void setIsCore(String isCore) {
		this.isCore = isCore;
	}

	public String getCreateUserAccount() {
		return createUserAccount;
	}

	public void setCreateUserAccount(String createUserAccount) {
		this.createUserAccount = createUserAccount;
	}

	
}
