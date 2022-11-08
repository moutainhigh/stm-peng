package com.mainsteam.stm.profilelib.bean;

import java.io.Serializable;
import java.util.HashSet;

public class MetricThresholdKey implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2348217570876907160L;
	
	private HashSet<Long> thresholdIds;

	public MetricThresholdKey(HashSet<Long> thresholdIds){
		this.thresholdIds = thresholdIds;
	}
	
	public HashSet<Long> getThresholdIds() {
		return thresholdIds;
	}

	public void setThresholdIds(HashSet<Long> thresholdIds) {
		this.thresholdIds = thresholdIds;
	} 

}
