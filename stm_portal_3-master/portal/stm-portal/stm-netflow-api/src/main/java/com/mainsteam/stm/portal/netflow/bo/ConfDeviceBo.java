package com.mainsteam.stm.portal.netflow.bo;

public class ConfDeviceBo {

	private Integer id;
	private String ip;
	private String name;
	private String snmpPort;
	private String readCommumity;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getSnmpPort() {
		return snmpPort;
	}

	public void setSnmpPort(String snmpPort) {
		this.snmpPort = snmpPort;
	}

	public String getReadCommumity() {
		return readCommumity;
	}

	public void setReadCommumity(String readCommumity) {
		this.readCommumity = readCommumity;
	}

}
