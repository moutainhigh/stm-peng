package com.mainsteam.stm.plugin.xen.bo;

import java.util.ArrayList;
import java.util.List;

public class XenResourceNode {
	
	private String resourceId, uuid, ip, name, type;
	
	private List<XenResourceNode> childTrees;
	private List<XenSubResourceNode> subResources;
	
	public XenResourceNode(String resourceID, String uuid, String ip, String name, String type) {
		super();
		this.resourceId = resourceID;
		this.uuid = uuid;
		this.ip = ip;
		this.name = name;
		this.type = type;
		this.childTrees = new ArrayList<XenResourceNode>();
		this.subResources = new ArrayList<XenSubResourceNode>();
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

	public List<XenResourceNode> getChildTrees() {
		return childTrees;
	}

	public void setChildTrees(List<XenResourceNode> childTrees) {
		this.childTrees = childTrees;
	}

	public List<XenSubResourceNode> getSubResources() {
		return subResources;
	}

	public void setSubResources(List<XenSubResourceNode> subResources) {
		this.subResources = subResources;
	}

	@Override
	public String toString() {
		return "XenResourceNode [resourceId=" + resourceId + ", uuid=" + uuid + ", ip=" + ip + ", name=" + name + ", childTrees=" + childTrees + ", subResources="
				+ subResources + "]";
	}

}
