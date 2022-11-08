package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;

import com.mainsteam.stm.caplib.dict.DiscoverWayEnum;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;

public class ResourceMonitorBo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8261111498483850117L;

	
	private String resourceId;
	
	private String resourceName;
	
	private Long instanceId;
	
	private String categoryId;
	
	private String showName;
	
	private Long domainId;
	
	private InstanceLifeStateEnum lifeState;
	
	private DiscoverWayEnum discoverWay;
	
	private String instanceStatus;
	
	private String ip;
	
	private boolean hasRight;
	
	private boolean hasTelSSHParams;
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

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public Long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public InstanceLifeStateEnum getLifeState() {
		return lifeState;
	}

	public void setLifeState(InstanceLifeStateEnum lifeState) {
		this.lifeState = lifeState;
	}

	public DiscoverWayEnum getDiscoverWay() {
		return discoverWay;
	}

	public void setDiscoverWay(DiscoverWayEnum discoverWay) {
		this.discoverWay = discoverWay;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean getHasRight() {
		return hasRight;
	}

	public void setHasRight(boolean hasRight) {
		this.hasRight = hasRight;
	}

	public boolean getHasTelSSHParams() {
		return hasTelSSHParams;
	}

	public void setHasTelSSHParams(boolean hasTelSSHParams) {
		this.hasTelSSHParams = hasTelSSHParams;
	}

	public String getCpuAvailability() {
		return cpuAvailability;
	}

	public void setCpuAvailability(String cpuAvailability) {
		this.cpuAvailability = cpuAvailability;
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

	public String getMemoryAvailability() {
		return memoryAvailability;
	}

	public void setMemoryAvailability(String memoryAvailability) {
		this.memoryAvailability = memoryAvailability;
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

	public String getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}

	public String getInstanceStatus() {
		return instanceStatus;
	}

	public String getDcsGroupName() {
		return dcsGroupName;
	}

	public void setDcsGroupName(String dcsGroupName) {
		this.dcsGroupName = dcsGroupName;
	}

	public void setInstanceStatus(String instanceStatus) {
		this.instanceStatus = instanceStatus;
	}

	@Override
	public boolean equals(Object arg0) {
		return ((ResourceMonitorBo)arg0).getInstanceId().longValue() == this.getInstanceId();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
		return result;
	}

}
