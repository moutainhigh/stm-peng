package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;
import java.util.List;

public class ProfileInstanceRelation implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5228392769860016338L;
	/**
	 * 关系主键Id
	 */
	private long ProfileInstanceRelation_mkId;

	/**
	 * 策略Id
	 */
	private long profileId;
	/**
	 * 策略对应资源实例
	 */
	private List<Instance> instances;

	public long getProfileInstanceRelation_mkId() {
		return ProfileInstanceRelation_mkId;
	}

	public void setProfileInstanceRelation_mkId(long profileInstanceRelation_mkId) {
		ProfileInstanceRelation_mkId = profileInstanceRelation_mkId;
	}
	
	public long getProfileId() {
		return profileId;
	}

	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}

	public List<Instance> getInstances() {
		return instances;
	}

	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}
}
