package com.mainsteam.stm.profilelib.bean;

import java.io.Serializable;
import java.util.HashSet;

public class ProfileMetricKey implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5380984417077616421L;

	private HashSet<String> metriceIds;

	public ProfileMetricKey(HashSet<String> metricIds){
		this.metriceIds = metricIds;
	}
	
	public HashSet<String> getMetriceIds() {
		return metriceIds;
	}
	
	public void setMetriceIds(HashSet<String> metriceIds) {
		this.metriceIds = metriceIds;
	}
}
