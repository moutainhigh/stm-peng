package com.mainsteam.stm.portal.vm.web.vo;

public class VirtualVMVo {//VirtualVMVo
private String vMareName;
private String osVersion;
private String DiskAssignedSpace;
private String DiskAssignedSpace_Unit;
//private String hostCPUGhz;
//private String hostCPUGhz_Unit;
private String cpuNum;
private String MEMVMSize;
private String MEMVMSize_Unit;
private String hostPC;
private String vCenter;
public String getvMareName() {
	return vMareName;
}
public void setvMareName(String vMareName) {
	this.vMareName = vMareName;
}
public String getOsVersion() {
	return osVersion;
}
public void setOsVersion(String osVersion) {
	this.osVersion = osVersion;
}
public String getDiskAssignedSpace() {
	return DiskAssignedSpace;
}
public void setDiskAssignedSpace(String diskAssignedSpace) {
	DiskAssignedSpace = diskAssignedSpace;
}
public String getDiskAssignedSpace_Unit() {
	return DiskAssignedSpace_Unit;
}
public void setDiskAssignedSpace_Unit(String diskAssignedSpace_Unit) {
	DiskAssignedSpace_Unit = diskAssignedSpace_Unit;
}
public String getCpuNum() {
	return cpuNum;
}
public void setCpuNum(String cpuNum) {
	this.cpuNum = cpuNum;
}
public String getMEMVMSize() {
	return MEMVMSize;
}
public void setMEMVMSize(String mEMVMSize) {
	MEMVMSize = mEMVMSize;
}
public String getMEMVMSize_Unit() {
	return MEMVMSize_Unit;
}
public void setMEMVMSize_Unit(String mEMVMSize_Unit) {
	MEMVMSize_Unit = mEMVMSize_Unit;
}
public String getHostPC() {
	return hostPC;
}
public void setHostPC(String hostPC) {
	this.hostPC = hostPC;
}
public String getvCenter() {
	return vCenter;
}
public void setvCenter(String vCenter) {
	this.vCenter = vCenter;
}

}
