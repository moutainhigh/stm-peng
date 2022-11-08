package com.mainsteam.stm.pluginsession.parameter;

/**
 * 集群 实体
 * 
 * @author yuanlb
 *
 */
public class JBrokerClusterParameter {
	private String name;
	private String cpufrequency;// cpu内频
	private String datacenter;// 数据中心 名
	private String path;
	private int cpu;
	private short cpucount;
	private long memory;
	private String evcMode;// 以太网虚连接
	private boolean haEnabled;// 开启HA与否
	private boolean drsEnabled;// 开启drs与否
//	CLUSTER指标如下：
//	可用性指标
//	性能指标：
//	CPU使用情况（%）
//	内存使用情况（%）
//	信息指标：
//	CPU总计（MHz)
//	CPU使用情况（MHz)
//	内存总计（MB）
//	vSphere DRS: 开启
//	vSphere HA:  开启
//	vMware EVC: 已禁用
//	主机数
//	处理器总数
//	总数据存储数
//	虚拟机和模板数
//	总存储（TB）

	@Override
	public String toString() {
		return "\n(群集) [(名称)name=" + name
				+ ", (数据中心)datacenter=" + datacenter + ", (路径)path=" + path
				+ ", (处理器)cpu=" + cpu + ", (处理器个数)cpucount=" + cpucount
				+ ", (内存)memory=" + memory + ", (evc模式)evcMode=" + evcMode
				+ ", (HA是否开启)haEnabled=" + haEnabled + ", (DRS是否开启)drsEnabled="
				+ drsEnabled + "]\n";
	}

	public void setCpu(int cpu) {
		this.cpu = cpu;
	}

	public String getEvcMode() {
		return evcMode;
	}

	public void setEvcMode(String evcMode) {
		this.evcMode = evcMode;
	}

	public boolean isDrsEnabled() {
		return drsEnabled;
	}

	public void setDrsEnabled(boolean drsEnabled) {
		this.drsEnabled = drsEnabled;
	}

	public void setCpucount(short cpucount) {
		this.cpucount = cpucount;
	}

	public String getName() {
		return name;
	}

	public String getDatacenter() {
		return datacenter;
	}

	public void setDatacenter(String datacenter) {
		this.datacenter = datacenter;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getMemory() {
		return memory;
	}

	public void setMemory(long memory) {
		this.memory = memory;
	}

	public int getCpu() {
		return cpu;
	}

	public short getCpucount() {
		return cpucount;
	}

	public boolean isHaEnabled() {
		return haEnabled;
	}

	public void setHaEnabled(boolean haEnabled) {
		this.haEnabled = haEnabled;
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

	/**
	 * 临时 空实现 急群众添加服务器
	 */
	public void addServer(String server) {
	}
}
