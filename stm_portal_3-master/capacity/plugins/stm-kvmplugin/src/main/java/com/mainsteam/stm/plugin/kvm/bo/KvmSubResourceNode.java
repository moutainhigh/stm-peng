package com.mainsteam.stm.plugin.kvm.bo;

/**
 * 
 * @author yuanlb TODO desc 下午3:16:19
 */
public class KvmSubResourceNode {
	private String resourceID, uuid, name;

	public KvmSubResourceNode(String resourceID, String uuid, String name) {
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
		return "FCSubResourceNode [resourceID=" + resourceID + ", uuid=" + uuid
				+ ", name=" + name + "]";
	}
}
