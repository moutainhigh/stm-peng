package com.mainsteam.stm.portal.vm.bo;

import java.io.Serializable;

public class VmResourceBo implements Serializable {

	private static final long serialVersionUID = 3340648983796680788L;
	/**
	 * id
	 */
	private Long id;
	/**
	 * IP
	 */
	private String ip;
	/**
	 * 资源名称
	 */
	private String sourceName;
	
	private String showName;
	/**
	 * CPU百分比
	 */
	private String cpuPercent;
	/**
	 * CPU百分比状态
	 */
	private String cpuPercentState;
	/**
	 * CPU百分比是否告警
	 */
	private boolean cpuPercentIsAlarm;
	/**
	 * 内存百分比
	 */
	private String memPercent;
	/**
	 * 内存百分比状态
	 */
	private String memPercentState;
	/**
	 * 内存百分比是否告警
	 */
	private boolean memPercentIsAlarm;
	/**
	 * CPU使用情况
	 */
	private String cpuRate;
	/**
	 * CPU使用情况状态
	 */
	private String cpuRateState;
	/**
	 * CPU使用情况是否告警
	 */
	private boolean cpuRateIsAlarm;
	/**
	 * 内存使用情况
	 */
	private String memRate;
	/**
	 * 内存使用情况状态
	 */
	private String memRateState;
	/**
	 * 内存使用情况是否告警
	 */
	private boolean memRateIsAlarm;
	/**
	 * 总CPU资源
	 */
	private String allCpu;
	/**
	 * 总内存
	 */
	private String allMem;
	/**
	 * 空间使用情况
	 */
	private String datastoreRate;
	/**
	 * 空间使用情况状态
	 */
	private String datastoreRateState;
	/**
	 * 空间使用情况是否告警
	 */
	private boolean datastoreRateIsAlarm;
	/**
	 * 总存储
	 */
	private String allDatastore;
	/**
	 * 可用空间
	 */
	private String freeDatastore;
	/**
	 * 主机数
	 */
	private Integer exsiTotal;
	/**
	 * 虚拟机数
	 */
	private Integer vmTotal;
	
	private Integer vmTotalForPool;
	/**
	 * Cluster
	 */
	private String cluster;
	/**
	 * 客户机操作系统
	 */
	private String vmOS;
	/**
	 * 主机
	 */
	private String exsi;
	/**
	 * 数据中心
	 */
	private String dataCenter;
	/**
	 * Vcenter
	 */
	private String vCenter;
	/**
	 * Domain name
	 */
	private String domainName;
	/**
	 * DCS Group Name
	 */
	private String dcsGroupName;
	
	// 查询条件
	
	/**
	 * 资源状态
	 */
	private String resourceStatus;
	/**
	 * IP or name
	 */
	private String iPorName;
	/**
	 * Domain ID
	 */
	private Long domainId;
	/**
	 * resourceId ID
	 */
	private String resourceId;
	/**
	 * categoryId ID
	 */
	private String categoryId;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * 总空间
	 */
	private String physicalSize;
	/**
	 * 已使用空间
	 */
	private String physicalUtilisation;
	/**
	 * 空间利用率
	 */
	private String physicalRate;
	/**
	 * 空间利用率状态
	 */
	private String physicalRateState;
	/**
	 * 空间利用率是否告警
	 */
	private boolean physicalRateIsAlarm;
	/**
	 * 运行时间
	 */
	private String runtime;
	/**
	 * 存储类型
	 */
	private String type;
	
	private String uuid;
	
	private String resourcePoolUuid;
	
	/**
	 * 责任人
	 */
    private String liablePerson;
	
	public String getLiablePerson() {
		return liablePerson;
	}

	public void setLiablePerson(String liablePerson) {
		this.liablePerson = liablePerson;
	}

	public String getResourcePoolUuid() {
		return resourcePoolUuid;
	}

	public void setResourcePoolUuid(String resourcePoolUuid) {
		this.resourcePoolUuid = resourcePoolUuid;
	}

	public Integer getVmTotalForPool() {
		return vmTotalForPool;
	}

	public void setVmTotalForPool(Integer vmTotalForPool) {
		this.vmTotalForPool = vmTotalForPool;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getCpuPercent() {
		return cpuPercent;
	}

	public void setCpuPercent(String cpuPercent) {
		this.cpuPercent = cpuPercent;
	}

	public String getCpuPercentState() {
		return cpuPercentState;
	}

	public void setCpuPercentState(String cpuPercentState) {
		this.cpuPercentState = cpuPercentState;
	}

	public boolean isCpuPercentIsAlarm() {
		return cpuPercentIsAlarm;
	}

	public void setCpuPercentIsAlarm(boolean cpuPercentIsAlarm) {
		this.cpuPercentIsAlarm = cpuPercentIsAlarm;
	}

	public String getMemPercent() {
		return memPercent;
	}

	public void setMemPercent(String memPercent) {
		this.memPercent = memPercent;
	}

	public String getMemPercentState() {
		return memPercentState;
	}

	public void setMemPercentState(String memPercentState) {
		this.memPercentState = memPercentState;
	}

	public boolean isMemPercentIsAlarm() {
		return memPercentIsAlarm;
	}

	public void setMemPercentIsAlarm(boolean memPercentIsAlarm) {
		this.memPercentIsAlarm = memPercentIsAlarm;
	}

	public String getCpuRate() {
		return cpuRate;
	}

	public void setCpuRate(String cpuRate) {
		this.cpuRate = cpuRate;
	}

	public String getCpuRateState() {
		return cpuRateState;
	}

	public void setCpuRateState(String cpuRateState) {
		this.cpuRateState = cpuRateState;
	}

	public boolean isCpuRateIsAlarm() {
		return cpuRateIsAlarm;
	}

	public void setCpuRateIsAlarm(boolean cpuRateIsAlarm) {
		this.cpuRateIsAlarm = cpuRateIsAlarm;
	}

	public String getMemRate() {
		return memRate;
	}

	public void setMemRate(String memRate) {
		this.memRate = memRate;
	}

	public String getMemRateState() {
		return memRateState;
	}

	public void setMemRateState(String memRateState) {
		this.memRateState = memRateState;
	}

	public boolean isMemRateIsAlarm() {
		return memRateIsAlarm;
	}

	public void setMemRateIsAlarm(boolean memRateIsAlarm) {
		this.memRateIsAlarm = memRateIsAlarm;
	}

	public String getAllCpu() {
		return allCpu;
	}

	public void setAllCpu(String allCpu) {
		this.allCpu = allCpu;
	}

	public String getAllMem() {
		return allMem;
	}

	public void setAllMem(String allMem) {
		this.allMem = allMem;
	}

	public String getDatastoreRate() {
		return datastoreRate;
	}

	public void setDatastoreRate(String datastoreRate) {
		this.datastoreRate = datastoreRate;
	}

	public String getDatastoreRateState() {
		return datastoreRateState;
	}

	public void setDatastoreRateState(String datastoreRateState) {
		this.datastoreRateState = datastoreRateState;
	}

	public boolean isDatastoreRateIsAlarm() {
		return datastoreRateIsAlarm;
	}

	public void setDatastoreRateIsAlarm(boolean datastoreRateIsAlarm) {
		this.datastoreRateIsAlarm = datastoreRateIsAlarm;
	}

	public String getAllDatastore() {
		return allDatastore;
	}

	public void setAllDatastore(String allDatastore) {
		this.allDatastore = allDatastore;
	}

	public String getFreeDatastore() {
		return freeDatastore;
	}

	public void setFreeDatastore(String freeDatastore) {
		this.freeDatastore = freeDatastore;
	}

	public Integer getExsiTotal() {
		return exsiTotal;
	}

	public void setExsiTotal(Integer exsiTotal) {
		this.exsiTotal = exsiTotal;
	}

	public Integer getVmTotal() {
		return vmTotal;
	}

	public void setVmTotal(Integer vmTotal) {
		this.vmTotal = vmTotal;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public String getVmOS() {
		return vmOS;
	}

	public void setVmOS(String vmOS) {
		this.vmOS = vmOS;
	}

	public String getExsi() {
		return exsi;
	}

	public void setExsi(String exsi) {
		this.exsi = exsi;
	}

	public String getDataCenter() {
		return dataCenter;
	}

	public void setDataCenter(String dataCenter) {
		this.dataCenter = dataCenter;
	}

	public String getvCenter() {
		return vCenter;
	}

	public void setvCenter(String vCenter) {
		this.vCenter = vCenter;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getDcsGroupName() {
		return dcsGroupName;
	}

	public void setDcsGroupName(String dcsGroupName) {
		this.dcsGroupName = dcsGroupName;
	}

	public String getResourceStatus() {
		return resourceStatus;
	}

	public void setResourceStatus(String resourceStatus) {
		this.resourceStatus = resourceStatus;
	}

	public String getiPorName() {
		return iPorName;
	}

	public void setiPorName(String iPorName) {
		this.iPorName = iPorName;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhysicalSize() {
		return physicalSize;
	}

	public void setPhysicalSize(String physicalSize) {
		this.physicalSize = physicalSize;
	}

	public String getPhysicalUtilisation() {
		return physicalUtilisation;
	}

	public void setPhysicalUtilisation(String physicalUtilisation) {
		this.physicalUtilisation = physicalUtilisation;
	}

	public String getPhysicalRate() {
		return physicalRate;
	}

	public void setPhysicalRate(String physicalRate) {
		this.physicalRate = physicalRate;
	}

	public String getPhysicalRateState() {
		return physicalRateState;
	}

	public void setPhysicalRateState(String physicalRateState) {
		this.physicalRateState = physicalRateState;
	}

	public boolean isPhysicalRateIsAlarm() {
		return physicalRateIsAlarm;
	}

	public void setPhysicalRateIsAlarm(boolean physicalRateIsAlarm) {
		this.physicalRateIsAlarm = physicalRateIsAlarm;
	}
	
	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public String getRuntime() {
		return runtime;
	}

	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/** cpu内核数 */
	private String cpuNum;
	/** 内存容量 */
	private String memSize;
	/** aliyunVm所在域 */
	private String regeion;
	/** 创建时间 */
	private String createTime;
	/** kylinVm所属项目 */
	private String projectName;
	
	public String getCpuNum() {
		return cpuNum;
	}
	public void setCpuNum(String cpuNum) {
		this.cpuNum = cpuNum;
	}
	public String getMemSize() {
		return memSize;
	}
	public void setMemSize(String memSize) {
		this.memSize = memSize;
	}
	public String getRegeion() {
		return regeion;
	}
	public void setRegeion(String regeion) {
		this.regeion = regeion;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}
