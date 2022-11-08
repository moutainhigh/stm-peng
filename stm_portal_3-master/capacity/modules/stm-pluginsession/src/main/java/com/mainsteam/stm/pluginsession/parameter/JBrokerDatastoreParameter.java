package com.mainsteam.stm.pluginsession.parameter;

/**
 * 存储 实体
 * 
 * @author yuanlb
 *
 */
public class JBrokerDatastoreParameter {
	@Override
	public String toString() {
		return "\n(存储) [(名称)name=" + name
				+ ", (状态)status=" + status + ", (性能)capacity=" + capacity
				+ ", (可用空间)freeSpace=" + freeSpace + ", (最大容量)maxFileSize="
				+ maxFileSize + ", (最小容量)mixFileSize=" + mixFileSize
				+ ", (连接地址)url=" + url + ", (类型)type=" + type
				+ ", (版本)version=" + version + ", (vmfsUUID)vmfsUUID="
				+ vmfsUUID + "]\n";
	}

	private String name;
	private String cpufrequency;// cpu内频

	private String status;
	private long capacity;// 性能
	private double freeSpace;// 可用空间
	private double maxFileSize;// 最大容量
	private double mixFileSize;// 最小容量
	private String url;
	private String type;
	private String version;
	private String vmfsUUID;

	
//	数据存储指标如下：
//	性能指标：
//	空间使用率（%）
//	控制标准化滞后时间（微秒）
//	控制汇总IOPS（个）
//	信息指标：
//	已用空间（GB）1T
//	可用空间（GB）500G
//	类型 VMFS
//	所连主机数目 2
//	虚拟机和模板数 20
//	容量（GB）1.5T
	public String getVmfsUUID() {
		return vmfsUUID;
	}

	public void setVmfsUUID(String vmfsUUID) {
		this.vmfsUUID = vmfsUUID;
	}

	public String getName() {
		return name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getCapacity() {
		return capacity;
	}

	public void setCapacity(long capacity) {
		this.capacity = capacity;
	}

	public double getFreeSpace() {
		return freeSpace;
	}

	public void setFreeSpace(double freeSpace) {
		this.freeSpace = freeSpace;
	}

	public double getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(double maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public double getMixFileSize() {
		return mixFileSize;
	}

	public void setMixFileSize(double mixFileSize) {
		this.mixFileSize = mixFileSize;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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
	 * 临时 空实现存储中 增加disk
	 */
	public void addDisk(JBrokerDiskParameter jBrokerDiskParameter) {
		// TODO Auto-generated method stub
	}

	/**
	 * 临时 空实现 存储中增加vm
	 */
	public void addVm(String name2) {
		// TODO Auto-generated method stub

	}

	// /**
	// * 临时 空实现 存储容器中添加 存储
	// */
	// public void add(JBrokerDatastoreParameter datastore) {
	// // TODO Auto-generated method stub
	// }
}
