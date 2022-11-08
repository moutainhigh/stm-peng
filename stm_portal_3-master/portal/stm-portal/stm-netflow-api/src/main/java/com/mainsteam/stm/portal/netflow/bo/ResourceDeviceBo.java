package com.mainsteam.stm.portal.netflow.bo;

public class ResourceDeviceBo {

	private long id;
	private String name;//sort,排序的列的名字
	private String ip;
	private String type;//order
	private String deviceType;//为了不改变原来的代码结构，并且不添加额外的无用的字段，页面上的查询字段就使用这个字段来对应页面的deviceSearchname
	private String manufacturers;
	private String protocolVersion;
	private int dbId;

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getManufacturers() {
		return manufacturers;
	}

	public void setManufacturers(String manufacturers) {
		this.manufacturers = manufacturers;
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public int getDbId() {
		return dbId;
	}

	public void setDbId(int dbId) {
		this.dbId = dbId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInfo() {
		return (this.id + "|" + this.name + "|" + this.ip + "|" + this.type
				+ "|" + this.manufacturers).replaceAll("\"", "“");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ResourceDeviceBo) {
			ResourceDeviceBo op = (ResourceDeviceBo) obj;
			return this.ip != null ? this.ip == op.ip || this.ip.equals(op.ip)
					: this.ip == op.ip;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.ip == null ? 0 : this.ip.hashCode();
	}

}
