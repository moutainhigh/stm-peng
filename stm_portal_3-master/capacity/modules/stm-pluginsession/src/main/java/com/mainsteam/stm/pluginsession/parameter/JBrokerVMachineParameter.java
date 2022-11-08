package com.mainsteam.stm.pluginsession.parameter;

/**
 * @don 虚拟机 vo
 * @author yuanlb
 *
 */
public class JBrokerVMachineParameter {
	// 属性都是乱的 待修改
	private String name;
	/**
	 * cpu频率
	 */
	private String cpufrequency;
	private String uuid;// 为id
	private String mac;// 网卡地址
	private String portgroup;// 端口组
	private String vmPath;// 虚拟机路径
	private String vmID;// 虚拟机编号
	/**
	 * vof模板？
	 */
	private String model;
	private String power;// 电源
	private String health;// 健康状况
	private String version;
	private String vmIP;// 虚拟机IP
	private String cpucount;// CPU数量
	/**
	 * 电源状态
	 */
	private String powerStatus;

	
//	VM指标如下：
//	可用性指标
//	性能指标
//	CPU使用情况（MHz)
//	CPU使用情况(%)
//	活动内存（kbytes)
//	授权量（kbytes)
//	客户机内存（%）
//	磁盘读取速度（KBps)
//	磁盘写入速度 (KBps)
//	磁盘使用情况 (KBps)
//	网络数据接收速度（KBps)
//	网络数据传输速度(KBps)
//	网络使用情况(KBps)
//	信息指标
//	客户机操作系统
//	CPU
//	内存大小
//	IP地址
//	EVC模式
//	所在主机
//	连续运行时间
	
	public String getPowerStatus() {
		return powerStatus;
	}

	public void setPowerStatus(String powerStatus) {
		this.powerStatus = powerStatus;
	}

	@Override
	public String toString() {
		return "\n(虚拟机) [(虚拟机名称)name=" + name
				+ ", (处理器外频)cpufrequency=" + cpufrequency + ", (网卡地址)mac="
				+ mac + ", (端口组)portgroup=" + portgroup + ", (路径)vmPath="
				+ vmPath + ", (编号)vmID=" + vmID + ", (魔板)model=" + model
				+ ", (电源)Power=" + power + ", (健康度)Health=" + health
				+ ", (版本)version=" + version + ", (IP地址)vmIP=" + vmIP
				+ ", (CPU数量)cpucount=" + cpucount + "]\n";
	}

	public String getName() {
		return name;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getPortgroup() {
		return portgroup;
	}

	public void setPortgroup(String portgroup) {
		this.portgroup = portgroup;
	}

	public String getVmPath() {
		return vmPath;
	}

	public void setVmPath(String vmPath) {
		this.vmPath = vmPath;
	}

	public String getVmID() {
		return vmID;
	}

	public void setVmID(String vmID) {
		this.vmID = vmID;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}




	public String getPower() {
		return power;
	}

	public void setPower(String power) {
		this.power = power;
	}

	public String getHealth() {
		return health;
	}

	public void setHealth(String health) {
		this.health = health;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVmIP() {
		return vmIP;
	}

	public void setVmIP(String vmIP) {
		this.vmIP = vmIP;
	}

	public String getCpucount() {
		return cpucount;
	}

	public void setCpucount(String cpucount) {
		this.cpucount = cpucount;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCpufrequency() {
		return cpufrequency;
	}

	public void setCpufrequency(String cpufrequency) {
		this.cpufrequency = cpufrequency;
	}
}
