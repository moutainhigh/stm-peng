package com.mainsteam.stm.camera.bo;

import java.io.Serializable;

public class CameraDcListVo implements Serializable {

	private static final long serialVersionUID = 3960470591378536056L;

	private Long id;
	private String IP;
	private String dbType;
	private String dbName;
	private String jdbcPort;
	private Long domainId;
	private String domainName;
	private String discoverNode;
	private String discoverNodeName;

	private String dbPassword;
	private String dbUsername;
	private String nodeGroupId;
	private String otherParams;
	private String resourceId;

	private Boolean ifAutoRefresh;
	private int autoRefreshCycleDay;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getJdbcPort() {
		return jdbcPort;
	}

	public void setJdbcPort(String jdbcPort) {
		this.jdbcPort = jdbcPort;
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

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public String getDbUsername() {
		return dbUsername;
	}

	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}

	public String getNodeGroupId() {
		return nodeGroupId;
	}

	public void setNodeGroupId(String nodeGroupId) {
		this.nodeGroupId = nodeGroupId;
	}

	public String getOtherParams() {
		return otherParams;
	}

	public void setOtherParams(String otherParams) {
		this.otherParams = otherParams;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public Boolean getIfAutoRefresh() {
		return ifAutoRefresh;
	}

	public void setIfAutoRefresh(Boolean ifAutoRefresh) {
		this.ifAutoRefresh = ifAutoRefresh;
	}

	public int getAutoRefreshCycleDay() {
		return autoRefreshCycleDay;
	}

	public void setAutoRefreshCycleDay(int autoRefreshCycleDay) {
		this.autoRefreshCycleDay = autoRefreshCycleDay;
	}

}
