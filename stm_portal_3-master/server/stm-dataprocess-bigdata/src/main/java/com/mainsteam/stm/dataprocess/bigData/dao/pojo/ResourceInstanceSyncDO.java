package com.mainsteam.stm.dataprocess.bigData.dao.pojo;


public class ResourceInstanceSyncDO {
	//[ResourceInstance]instanceId=111,instanceName=name,instanceShowName=show,parented=111,instanceType=111,resourceid=ttt,ip=127.0.0.1;
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
	private String ip;
	
	private String profileID;
	
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
	

	private String instanceShowName;
	
	
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getProfileID() {
		return profileID;
	}

	public void setProfileID(String profileID) {
		this.profileID = profileID;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
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

}
