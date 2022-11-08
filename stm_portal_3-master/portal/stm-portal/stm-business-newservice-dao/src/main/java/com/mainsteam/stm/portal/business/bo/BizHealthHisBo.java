package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;
import java.util.Date;

public class BizHealthHisBo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6781448191234481833L;

	//id
	private long id;
	
	//业务ID
	private long bizId;
	
	//业务健康度
	private int bizHealth;
	
	//业务状态(0正常1警告,2严重,3致命)
	private int bizStatus;
	
	//业务更新状态时间
	private Date bizChangeTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getBizId() {
		return bizId;
	}

	public void setBizId(long bizId) {
		this.bizId = bizId;
	}

	public int getBizHealth() {
		return bizHealth;
	}

	public void setBizHealth(int bizHealth) {
		this.bizHealth = bizHealth;
	}

	public int getBizStatus() {
		return bizStatus;
	}

	public void setBizStatus(int bizStatus) {
		this.bizStatus = bizStatus;
	}

	public Date getBizChangeTime() {
		return bizChangeTime;
	}

	public void setBizChangeTime(Date bizChangeTime) {
		this.bizChangeTime = bizChangeTime;
	}
	
}
