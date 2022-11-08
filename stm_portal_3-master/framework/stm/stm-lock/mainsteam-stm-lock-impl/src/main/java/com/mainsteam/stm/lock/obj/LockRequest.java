package com.mainsteam.stm.lock.obj;

import java.util.Date;


/** 
 * @author 作者：ziw
 * @date 创建时间：2016年12月22日 上午11:24:02
 * @version 1.0
 */
public class LockRequest {

	private String name;
	private String node;
	private Date expireTime;
	private Date currentTime;
	private Date requestTime;
	private boolean greed;
	private boolean requestLock;
	public LockRequest() {
	}
	public boolean isGreed() {
		return greed;
	}
	
	public boolean isRequestLock() {
		return requestLock;
	}
	public void setRequestLock(boolean requestLock) {
		this.requestLock = requestLock;
	}
	public void setGreed(boolean greed) {
		this.greed = greed;
	}
	public LockRequest(LockRequest request) {
		this.name = request.name;
		this.node = request.node;
		this.expireTime = request.expireTime;
		this.currentTime = request.currentTime;
		this.requestTime = request.requestTime;
		this.greed = request.greed;
	}
	
	public String getNode() {
		return node;
	}


	public void setNode(String node) {
		this.node = node;
	}


	public Date getRequestTime() {
		return requestTime;
	}


	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}


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

	public Date getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
	}
	@Override
	public String toString() {
		return "LockRequest [name=" + name + ", node=" + node + ", expireTime="
				+ expireTime + ", currentTime=" + currentTime
				+ ", requestTime=" + requestTime + ", greed=" + greed + "]";
	}
}
