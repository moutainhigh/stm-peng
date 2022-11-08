package com.mainsteam.stm.lock.obj;

import java.util.Date;

public class LockPO {
	private String name;
	private String node;
	private Date expireTime;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getExpireTime() {
		return expireTime;
	}
	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
}
