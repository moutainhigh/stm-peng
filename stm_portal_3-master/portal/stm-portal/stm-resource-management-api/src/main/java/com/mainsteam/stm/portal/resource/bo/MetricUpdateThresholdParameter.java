package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;
import java.util.List;

public class MetricUpdateThresholdParameter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3629387381689791627L;

	private long profileId;
	
	private List<PortalThreshold> thresholdsMap;

	public long getProfileId() {
		return profileId;
	}

	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}

	public List<PortalThreshold> getThresholdsMap() {
		return thresholdsMap;
	}

	public void setThresholdsMap(List<PortalThreshold> thresholdsMap) {
		this.thresholdsMap = thresholdsMap;
	}
	
}
