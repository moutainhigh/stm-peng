package com.mainsteam.stm.topo.bo;

import org.springframework.util.StringUtils;

public class QueryNode {
	private Long id;
	private String ip;
	private String oid;
	private String mac;
	private String type;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getOid() {
		if(!StringUtils.hasText(oid)) return null;
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getMac() {
		if(!StringUtils.hasText(mac)) return null;
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getType() {
		if(!StringUtils.hasText(type)) return null;
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
