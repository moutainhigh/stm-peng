package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;
import java.util.List;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;


public class ResourceInstanceVo implements Serializable{
	
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
    
     /**
      * 内存利用率
      */
    private String memoryAvailability;

     /**
      * 响应时间
      */
    private Long responseTime;

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

	public Long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(Long responseTime) {
		this.responseTime = responseTime;
	}
    
    

}
