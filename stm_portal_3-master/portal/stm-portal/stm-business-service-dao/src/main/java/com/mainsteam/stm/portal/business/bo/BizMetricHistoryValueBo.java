package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;
import java.util.Date;

public class BizMetricHistoryValueBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5143451848502405439L;

	private int value;
	
	private Date time;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	
	
}
