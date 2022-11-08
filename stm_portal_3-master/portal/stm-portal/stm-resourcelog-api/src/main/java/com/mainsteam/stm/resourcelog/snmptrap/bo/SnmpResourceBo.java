package com.mainsteam.stm.resourcelog.snmptrap.bo;

import java.io.Serializable;
import java.util.Date;

public class SnmpResourceBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1612226533854825820L;

	private String resourceId;
	
	private String ipAddress;
	
	private int strategyType;
	
	private Date lastDate;
	
	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getStrategyType() {
		return strategyType;
	}

	public void setStrategyType(int strategyType) {
		this.strategyType = strategyType;
	}

	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}
	
}
