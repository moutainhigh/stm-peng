package com.mainsteam.stm.profilelib.fault.obj;

import java.io.Serializable;
import java.util.List;

public class ProfileFaultRelation extends Profilefault implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3244440360207283065L;
	/**
	 * 策略下资源实例集合
	 */
	private List<ProfileFaultInstance> profileFaultInstances;
	/**
	 * 策略指标集合
	 */
	private List<ProfileFaultMetric> profileFaultMetrics;
	
	public List<ProfileFaultInstance> getProfileFaultInstances() {
		return profileFaultInstances;
	}
	public void setProfileFaultInstances(
			List<ProfileFaultInstance> profileFaultInstances) {
		this.profileFaultInstances = profileFaultInstances;
	}
	public List<ProfileFaultMetric> getProfileFaultMetrics() {
		return profileFaultMetrics;
	}
	public void setProfileFaultMetrics(List<ProfileFaultMetric> profileFaultMetrics) {
		this.profileFaultMetrics = profileFaultMetrics;
	}
	
	
}
