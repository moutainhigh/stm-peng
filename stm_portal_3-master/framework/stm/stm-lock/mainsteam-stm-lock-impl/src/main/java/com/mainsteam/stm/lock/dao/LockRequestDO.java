package com.mainsteam.stm.lock.dao;

import java.util.Date;

/** 
 * @author 作者：ziw
 * @date 创建时间：2016年12月30日 下午2:11:59
 * @version 1.0
 */
public class LockRequestDO {
	private String name;
	private String node;
	private Date currentTime;
	private Date requestTime;
	private int greed;
	public LockRequestDO() {
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public int getGreed() {
		return greed;
	}
	public void setGreed(int greed) {
		this.greed = greed;
	}
	public Date getCurrentTime() {
		return currentTime;
	}
	public Date getRequestTime() {
		return requestTime;
	}
	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
	}
	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}
}
