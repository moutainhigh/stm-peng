package com.mainsteam.stm.portal.netflow.po;

import java.util.Date;

public class DevicePo {

	private Integer id;
	private String name;
	private String ip;
	private String company;
	private String version;
	private String readCommunity;
	private String snmpPort;
	private String defaultName;
	private int confDeviceId;
	private Date createData;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getReadCommunity() {
		return readCommunity;
	}

	public void setReadCommunity(String readCommunity) {
		this.readCommunity = readCommunity;
	}

	public String getSnmpPort() {
		return snmpPort;
	}

	public void setSnmpPort(String snmpPort) {
		this.snmpPort = snmpPort;
	}

	public String getDefaultName() {
		return defaultName;
	}

	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}

	public int getConfDeviceId() {
		return confDeviceId;
	}

	public void setConfDeviceId(int confDeviceId) {
		this.confDeviceId = confDeviceId;
	}

	public Date getCreateData() {
		return createData;
	}

	public void setCreateData(Date createData) {
		this.createData = createData;
	}

}
