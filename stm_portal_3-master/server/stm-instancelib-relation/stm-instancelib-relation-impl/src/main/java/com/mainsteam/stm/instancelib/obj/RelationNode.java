package com.mainsteam.stm.instancelib.obj;

public class RelationNode {

	private long id;
	
	private long parentId;
	
	private boolean isDown;
	
	private boolean isRoot;
	
	private String parentIp;
	
	private String parentShowName;
	
	private String parentDeviceType;
	
	private String childDeviceName;
	
	private long childDeviceId;
	
	private String childDeviceType;

	public long getId() {
		return id;
	}

	public boolean isDown() {
		return isDown;
	}

	public boolean isRoot() {
		return isRoot;
	}


	public String getChildDeviceName() {
		return childDeviceName;
	}

	public long getChildDeviceId() {
		return childDeviceId;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setDown(boolean isDown) {
		this.isDown = isDown;
	}

	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}


	public void setChildDeviceName(String childDeviceName) {
		this.childDeviceName = childDeviceName;
	}

	public void setChildDeviceId(long childDeviceId) {
		this.childDeviceId = childDeviceId;
	}

	public long getParentId() {
		return parentId;
	}

	public String getParentIp() {
		return parentIp;
	}

	public String getParentShowName() {
		return parentShowName;
	}

	public String getParentDeviceType() {
		return parentDeviceType;
	}

	public String getChildDeviceType() {
		return childDeviceType;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public void setParentIp(String parentIp) {
		this.parentIp = parentIp;
	}

	public void setParentShowName(String parentShowName) {
		this.parentShowName = parentShowName;
	}

	public void setParentDeviceType(String parentDeviceType) {
		this.parentDeviceType = parentDeviceType;
	}

	public void setChildDeviceType(String childDeviceType) {
		this.childDeviceType = childDeviceType;
	}
}
