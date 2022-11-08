package com.mainsteam.stm.portal.vm.web.vo;

public class VirtualStorageVo {
private String storageName;
private String HostNum;
private String VMNum;
private String DataStorageVolume;
private String DataStorageVolume_Unit;
private String DataStorageFreeSpace;
private String DataStorageFreeSpace_Unit;
public String getStorageName() {
	return storageName;
}
public void setStorageName(String storageName) {
	this.storageName = storageName;
}
public String getHostNum() {
	return HostNum;
}
public void setHostNum(String hostNum) {
	HostNum = hostNum;
}
public String getVMNum() {
	return VMNum;
}
public void setVMNum(String vMNum) {
	VMNum = vMNum;
}
public String getDataStorageVolume() {
	return DataStorageVolume;
}
public void setDataStorageVolume(String dataStorageVolume) {
	DataStorageVolume = dataStorageVolume;
}
public String getDataStorageFreeSpace() {
	return DataStorageFreeSpace;
}
public void setDataStorageFreeSpace(String dataStorageFreeSpace) {
	DataStorageFreeSpace = dataStorageFreeSpace;
}
public String getDataStorageVolume_Unit() {
	return DataStorageVolume_Unit;
}
public void setDataStorageVolume_Unit(String dataStorageVolume_Unit) {
	DataStorageVolume_Unit = dataStorageVolume_Unit;
}
public String getDataStorageFreeSpace_Unit() {
	return DataStorageFreeSpace_Unit;
}
public void setDataStorageFreeSpace_Unit(String dataStorageFreeSpace_Unit) {
	DataStorageFreeSpace_Unit = dataStorageFreeSpace_Unit;
}

}
