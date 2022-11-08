package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;
import java.util.Map;

public class MetricChange implements Serializable {

	private static final long serialVersionUID = -6321751094060644860L;

	private long profileId;

	private Map<String,String> freq;
	
	private Map<String,Boolean> monitor;

	public long getProfileId() {
		return profileId;
	}

	public Map<String, String> getFreq() {
		return freq;
	}

	public Map<String, Boolean> getMonitor() {
		return monitor;
	}

	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}

	public void setFreq(Map<String, String> freq) {
		this.freq = freq;
	}

	public void setMonitor(Map<String, Boolean> monitor) {
		this.monitor = monitor;
	}
	
}
