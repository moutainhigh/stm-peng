package com.mainsteam.stm.plugin.kvm.bo;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author yuanlb TODO desc 下午3:16:10
 */
public class KvmResourceNode {
	private String resourceId, uuid, ip, name, type;

	private List<KvmResourceNode> childTrees;

	private List<KvmSubResourceNode> subResources;

	public KvmResourceNode(String resourceID, String uuid, String ip,
			String name, String type) {
		super();
		this.resourceId = resourceID;
		this.uuid = uuid;
		this.ip = ip;
		this.name = name;
		this.type = type;
		this.childTrees = new ArrayList<KvmResourceNode>();
		this.subResources = new ArrayList<KvmSubResourceNode>();
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<KvmResourceNode> getChildTrees() {
		return childTrees;
	}

	public void setChildTrees(List<KvmResourceNode> childTrees) {
		this.childTrees = childTrees;
	}

	public List<KvmSubResourceNode> getSubResources() {
		return subResources;
	}

	public void setSubResources(List<KvmSubResourceNode> subResources) {
		this.subResources = subResources;
	}

	@Override
	public String toString() {
		return "KvmResourceNode [resourceId=" + resourceId + ", uuid=" + uuid
				+ ", ip=" + ip + ", name=" + name + ", childTrees="
				+ childTrees + ", subResources=" + subResources + "]";
	}
}
