package com.mainsteam.stm.portal.netflow.bo;

public class ApplicationBo {

	private int id;
	private String name;
	private String protocolName;//用来存储查询条件applicationName
	private String ports;//用来存储查询的顺序order
	private String ips;//用来存储查询条件applicationFirstName

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProtocolName() {
		return protocolName;
	}

	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
	}

	public String getPorts() {
		return ports;
	}

	public void setPorts(String ports) {
		this.ports = ports;
	}

	public String getIps() {
		return ips;
	}

	public void setIps(String ips) {
		this.ips = ips;
	}

}
