package com.mainsteam.stm.topo.bo;

public class VlanBo {
	private Long id;
	private Long vlanId;
	private String portsName;
	private String portsIndex;
	private Long nodeId;
	private String vlanName;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getVlanId() {
		return vlanId;
	}
	public void setVlanId(Long vlanId) {
		this.vlanId = vlanId;
	}
	public String getPortsName() {
		return portsName;
	}
	public void setPortsName(String portsName) {
		this.portsName = portsName;
	}
	public String getPortsIndex() {
		return portsIndex;
	}
	public void setPortsIndex(String portsIndex) {
		this.portsIndex = portsIndex;
	}
	public Long getNodeId() {
		return nodeId;
	}
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}
	public String getVlanName() {
		return vlanName;
	}
	public void setVlanName(String vlanName) {
		this.vlanName = vlanName;
	}
}
