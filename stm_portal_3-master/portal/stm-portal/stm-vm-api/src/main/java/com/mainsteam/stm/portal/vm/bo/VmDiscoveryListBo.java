package com.mainsteam.stm.portal.vm.bo;


public class VmDiscoveryListBo {

	private Long instanceId;
	private String ip;
	private String resourceId;
	private String categoryId;
	private String typeName;
	private Long domainId;
	private String domainName;
	private String discoverNode;
	private String discoverNodeName;
	private String discoveryType;
	private String userName;
	private String password;
	private boolean ifAutoRefresh = false;
	private int  autoRefreshCycleDay = 0;

	
	public boolean isIfAutoRefresh() {
		return ifAutoRefresh;
	}

	public void setIfAutoRefresh(boolean ifAutoRefresh) {
		this.ifAutoRefresh = ifAutoRefresh;
	}

	public int getAutoRefreshCycleDay() {
		return autoRefreshCycleDay;
	}

	public void setAutoRefreshCycleDay(int autoRefreshCycleDay) {
		this.autoRefreshCycleDay = autoRefreshCycleDay;
	}

	public Long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getDiscoverNode() {
		return discoverNode;
	}

	public void setDiscoverNode(String discoverNode) {
		this.discoverNode = discoverNode;
	}

	public String getDiscoverNodeName() {
		return discoverNodeName;
	}

	public void setDiscoverNodeName(String discoverNodeName) {
		this.discoverNodeName = discoverNodeName;
	}

	public String getDiscoveryType() {
		return discoveryType;
	}

	public void setDiscoveryType(String discoveryType) {
		this.discoveryType = discoveryType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	private String regeion; //aliyunVm所在域
	private String project; //kylinVm所属项目
	public String getRegeion() {
		return regeion;
	}
	public void setRegeion(String regeion) {
		this.regeion = regeion;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}

}
