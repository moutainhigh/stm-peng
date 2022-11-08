package com.mainsteam.stm.home.workbench.availability.bo;

import java.io.Serializable;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;


public class AvailabilityBo implements Serializable{
	
	private static final long serialVersionUID = 1L;
private ResourceInstance resourceInstance;
	
	private String instanceStatus;
	
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

	public String getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}

	public ResourceInstance getResourceInstance() {
		return resourceInstance;
	}

	public void setResourceInstance(ResourceInstance resourceInstance) {
		this.resourceInstance = resourceInstance;
	}

	public String getInstanceStatus() {
		return instanceStatus;
	}

	public void setInstanceStatus(String instanceStatus) {
		this.instanceStatus = instanceStatus;
	}
	
	

	
}
