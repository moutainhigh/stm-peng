package com.mainsteam.stm.pluginsession.parameter;

import java.util.Arrays;

/**
 * 磁盘 实体
 * 
 * @author yuanlb
 *
 */
public class JBrokerDiskParameter {
	private String name;
	private String path;
	private String addresses[];
	private String diskName;

	private String IScsiName;

	@Override
	public String toString() {
		return "\n(磁盘) [(名称)name=" + name + ", (路径)path="
				+ path + ", (地址)addresses=" + Arrays.toString(addresses)
				+ ", (磁盘名称)diskName=" + diskName + ", (IScsi名称)IScsiName="
				+ IScsiName + "]\n";
	}

	public String getIScsiName() {
		return IScsiName;
	}

	public void setIScsiName(String iScsiName) {
		IScsiName = iScsiName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String[] getAddresses() {
		return addresses;
	}

	public void setAddresses(String[] addresses) {
		this.addresses = addresses;
	}

	public String getDiskName() {
		return diskName;
	}

	public void setDiskName(String diskName) {
		this.diskName = diskName;
	}
}
