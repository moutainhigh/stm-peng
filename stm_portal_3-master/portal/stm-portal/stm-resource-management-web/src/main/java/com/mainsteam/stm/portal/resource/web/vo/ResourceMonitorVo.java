package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;

/**
 * <li>文件名称: ResourceMonitorVo.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月28日
 * @author xhf
 */
public class ResourceMonitorVo implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
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
    
    private boolean cpuIsAlarm;
     /**
      * 内存利用率
      */
    private String memoryAvailability;
    
    private String memoryStatus;

    private boolean memoryIsAlarm;
	/**
      * 响应时间
      */
    private String responseTime;
	/**
	 * DCS名称
	 */
	private String dcsGroupName;
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
    
    private String isCustomResGroup;
    /**
     * 域ID
     */
    private Long domainId;
    
    private String domainName;
    /**
     * 根据IP或者名字查询
     */
    private String iPorName;
    
    private boolean hasRight;

    private String liablePerson;
    
    private String maintainStaus;
    
    private String maintainStartTime;
    
    private String sourceIp;
    
    private String distIP;
    
    public String getSourceIp() {
		return sourceIp;
	}

	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}

	public String getDistIP() {
		return distIP;
	}

	public void setDistIP(String distIP) {
		this.distIP = distIP;
	}

	private String maintainEndTime;  
	public String getMaintainStaus() {
		return maintainStaus;
	}

	public void setMaintainStaus(String maintainStaus) {
		this.maintainStaus = maintainStaus;
	}

	public String getMaintainStartTime() {
		return maintainStartTime;
	}

	public void setMaintainStartTime(String maintainStartTime) {
		this.maintainStartTime = maintainStartTime;
	}

	public String getMaintainEndTime() {
		return maintainEndTime;
	}

	public void setMaintainEndTime(String maintainEndTime) {
		this.maintainEndTime = maintainEndTime;
	}

	public String getLiablePerson() {
		return liablePerson;
	}

	public void setLiablePerson(String liablePerson) {
		this.liablePerson = liablePerson;
	}

	private boolean hasTelSSHParams;
	
    public boolean getHasTelSSHParams() {
		return hasTelSSHParams;
	}

	public void setHasTelSSHParams(boolean hasTelSSHParams) {
		this.hasTelSSHParams = hasTelSSHParams;
	}

	public boolean getHasRight() {
		return hasRight;
	}

	public void setHasRight(boolean hasRight) {
		this.hasRight = hasRight;
	}

	public String getiPorName() {
		return iPorName;
	}

	public void setiPorName(String iPorName) {
		this.iPorName = iPorName;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(String categoryIds) {
		this.categoryIds = categoryIds;
	}

	public String getIsCustomResGroup() {
		return isCustomResGroup;
	}

	public void setIsCustomResGroup(String isCustomResGroup) {
		this.isCustomResGroup = isCustomResGroup;
	}

	public String getCpuStatus() {
		return cpuStatus;
	}

	public void setCpuStatus(String cpuStatus) {
		this.cpuStatus = cpuStatus;
	}

	public boolean getCpuIsAlarm() {
		return cpuIsAlarm;
	}

	public void setCpuIsAlarm(boolean cpuIsAlarm) {
		this.cpuIsAlarm = cpuIsAlarm;
	}

	public String getMemoryStatus() {
		return memoryStatus;
	}

	public void setMemoryStatus(String memoryStatus) {
		this.memoryStatus = memoryStatus;
	}
	
	public boolean getMemoryIsAlarm() {
		return memoryIsAlarm;
	}

	public void setMemoryIsAlarm(boolean memoryIsAlarm) {
		this.memoryIsAlarm = memoryIsAlarm;
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

	public String getDcsGroupName() {
		return dcsGroupName;
	}

	public void setDcsGroupName(String dcsGroupName) {
		this.dcsGroupName = dcsGroupName;
	}

	public String getInstanceState() {
		return instanceState;
	}

	public void setInstanceState(String instanceState) {
		this.instanceState = instanceState;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

    
    
   
}
