package com.mainsteam.stm.plugin.xen.bo;

public class XenSubResourceNode {
	private String resourceID, uuid, name;

	public XenSubResourceNode(String resourceID, String uuid, String name) {
		super();
		this.resourceID = resourceID;
		this.uuid = uuid;
		this.name = name;
	}

	public String getResourceID() {
		return resourceID;
	}

	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "XenSubResourceNode [resourceID=" + resourceID + ", uuid=" + uuid + ", name=" + name + "]";
	}

}
