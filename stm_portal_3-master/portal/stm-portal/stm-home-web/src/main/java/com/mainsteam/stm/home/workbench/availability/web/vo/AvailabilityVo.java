package com.mainsteam.stm.home.workbench.availability.web.vo;

public class AvailabilityVo {
	/**
	 * id
	 */
	private Long id;
	/**
	 * 资源名称
	 */
	private String sourceName;
	
	/**
	 * IP地址
	 */
	private String ipAddress;
	
	/**
	 * 监控类型
	 */
	private String monitorType;

    /**
     * CPU利用率
     */
    private String cpuAvailability;
    
    private String cpuStatus;
    
     /**
      * 内存利用率
      */
    private String memoryAvailability;
    
    private String memoryStatus;

	/**
      * 响应时间
      */
    private String responseTime;
    /**
     * 资源状态
     */
    private String instanceState;
    /**
     * 资源状态(致命,严重,告警,正常,未知)
     */
    private String instanceStatus;
    
    private String categoryId;
    
    private String resourceId;
    
    private String categoryIds;
    
    public String getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(String categoryIds) {
		this.categoryIds = categoryIds;
	}

	public String getCpuStatus() {
		return cpuStatus;
	}

	public void setCpuStatus(String cpuStatus) {
		this.cpuStatus = cpuStatus;
	}

	public String getMemoryStatus() {
		return memoryStatus;
	}

	public void setMemoryStatus(String memoryStatus) {
		this.memoryStatus = memoryStatus;
	}
	public String getInstanceStatus() {
		return instanceStatus;
	}

	public void setInstanceStatus(String instanceStatus) {
		this.instanceStatus = instanceStatus;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getMonitorType() {
		return monitorType;
	}

	public void setMonitorType(String monitorType) {
		this.monitorType = monitorType;
	}

	public String getCpuAvailability() {
		return cpuAvailability;
	}

	public void setCpuAvailability(String cpuAvailability) {
		this.cpuAvailability = cpuAvailability;
	}

	public String getMemoryAvailability() {
		return memoryAvailability;
	}

	public void setMemoryAvailability(String memoryAvailability) {
		this.memoryAvailability = memoryAvailability;
	}

	public String getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}

	public String getInstanceState() {
		return instanceState;
	}

	public void setInstanceState(String instanceState) {
		this.instanceState = instanceState;
	}
}
