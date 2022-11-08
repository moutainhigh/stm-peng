package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;

public class BizMainDataBo extends BizMetricDataBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -739471017090240660L;
	
	
	private long bizId;
	
	private String bizName;
	
	private int bizStatus;
	
	private String managerName;
	
	private String phone;
	
	private String totalRunTime;
	
	public String getTotalRunTime() {
		return totalRunTime;
	}

	public void setTotalRunTime(String totalRunTime) {
		this.totalRunTime = totalRunTime;
	}

	public long getBizId() {
		return bizId;
	}

	public void setBizId(long bizId) {
		this.bizId = bizId;
	}

	public String getBizName() {
		return bizName;
	}

	public void setBizName(String bizName) {
		this.bizName = bizName;
	}

	public int getBizStatus() {
		return bizStatus;
	}

	public void setBizStatus(int bizStatus) {
		this.bizStatus = bizStatus;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
