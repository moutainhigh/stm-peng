package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.profilelib.obj.Threshold;

public class MetricUpdateParameterVo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -177936789093624457L;

	private long profileId;
	
	private Map<String, Boolean> monitorMap;
	
	private Map<String, Boolean> alarmsMap;
	
	private Map<String, String> frequencyValueMap;
	
	private List<Threshold> thresholdsMap;
	
	private Map<String, Integer> flappingValueMap;

	public long getProfileId() {
		return profileId;
	}

	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}

	public Map<String, Boolean> getMonitorMap() {
		return monitorMap;
	}

	public void setMonitorMap(Map<String, Boolean> monitorMap) {
		this.monitorMap = monitorMap;
	}

	public Map<String, Boolean> getAlarmsMap() {
		return alarmsMap;
	}

	public void setAlarmsMap(Map<String, Boolean> alarmsMap) {
		this.alarmsMap = alarmsMap;
	}

	public Map<String, String> getFrequencyValueMap() {
		return frequencyValueMap;
	}

	public void setFrequencyValueMap(Map<String, String> frequencyValueMap) {
		this.frequencyValueMap = frequencyValueMap;
	}

	public List<Threshold> getThresholdsMap() {
		return thresholdsMap;
	}

	public void setThresholdsMap(List<Threshold> thresholdsMap) {
		this.thresholdsMap = thresholdsMap;
	}

	public Map<String, Integer> getFlappingValueMap() {
		return flappingValueMap;
	}

	public void setFlappingValueMap(Map<String, Integer> flappingValueMap) {
		this.flappingValueMap = flappingValueMap;
	}
	
	
	
}
