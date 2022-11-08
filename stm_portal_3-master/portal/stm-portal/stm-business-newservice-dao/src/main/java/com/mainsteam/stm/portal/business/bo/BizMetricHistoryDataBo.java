package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class BizMetricHistoryDataBo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4247871839896781672L;

	private int status;
	
	private String threshold;
	
	private String curValue;
	
	private Date lastCollect;
	
	private String unit;
	
	private int frontFirstHealth;
	
	public int getFrontFirstHealth() {
		return frontFirstHealth;
	}

	public void setFrontFirstHealth(int frontFirstHealth) {
		this.frontFirstHealth = frontFirstHealth;
	}

	private List<BizMetricHistoryValueBo> values;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	public String getCurValue() {
		return curValue;
	}

	public void setCurValue(String curValue) {
		this.curValue = curValue;
	}

	public Date getLastCollect() {
		return lastCollect;
	}

	public void setLastCollect(Date lastCollect) {
		this.lastCollect = lastCollect;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public List<BizMetricHistoryValueBo> getValues() {
		return values;
	}

	public void setValues(List<BizMetricHistoryValueBo> values) {
		this.values = values;
	}
	
}
