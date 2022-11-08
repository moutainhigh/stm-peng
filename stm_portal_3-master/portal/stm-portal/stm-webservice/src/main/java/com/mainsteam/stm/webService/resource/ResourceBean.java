package com.mainsteam.stm.webService.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class ResourceBean {

	// 资源ID
	@XmlElement(required = true)
	private long id;

	// 资源名称
	@XmlElement(required = true)
	private String name;

	// 资源IP
	@XmlElement(required = true)
	private String ip;

	// 资源类型
	@XmlElement(required = true)
	private String type;

	// 供应商名称
	@XmlElement(required = true)
	private String vendorName;

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

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	
}
