package com.mainsteam.stm.portal.vm.web.vo;

public class VirtualHostVo {
private String ip;
private String TotalCPU;
private String TotalCPU_Unit;
private String TotalMemSize;
private String TotalMemSize_Unit;
private String DatastoreSize;
private String DatastoreSize_Unit;
private String VMNum;
private String VMotion;
private String EVC;
private String cluster;
private String vCenter;
public String getIp() {
	return ip;
}
public void setIp(String ip) {
	this.ip = ip;
}
public String getTotalCPU() {
	return TotalCPU;
}
public void setTotalCPU(String totalCPU) {
	TotalCPU = totalCPU;
}
public String getTotalCPU_Unit() {
	return TotalCPU_Unit;
}
public void setTotalCPU_Unit(String totalCPU_Unit) {
	TotalCPU_Unit = totalCPU_Unit;
}
public String getTotalMemSize() {
	return TotalMemSize;
}
public void setTotalMemSize(String totalMemSize) {
	TotalMemSize = totalMemSize;
}
public String getTotalMemSize_Unit() {
	return TotalMemSize_Unit;
}
public void setTotalMemSize_Unit(String totalMemSize_Unit) {
	TotalMemSize_Unit = totalMemSize_Unit;
}

public String getDatastoreSize() {
	return DatastoreSize;
}
public void setDatastoreSize(String datastoreSize) {
	DatastoreSize = datastoreSize;
}
public String getDatastoreSize_Unit() {
	return DatastoreSize_Unit;
}
public void setDatastoreSize_Unit(String datastoreSize_Unit) {
	DatastoreSize_Unit = datastoreSize_Unit;
}
public String getVMNum() {
	return VMNum;
}
public void setVMNum(String vMNum) {
	VMNum = vMNum;
}
public String getVMotion() {
	return VMotion;
}
public void setVMotion(String vMotion) {
	VMotion = vMotion;
}
public String getEVC() {
	return EVC;
}
public void setEVC(String eVC) {
	EVC = eVC;
}
public String getCluster() {
	return cluster;
}
public void setCluster(String cluster) {
	this.cluster = cluster;
}
public String getvCenter() {
	return vCenter;
}
public void setvCenter(String vCenter) {
	this.vCenter = vCenter;
}

}
