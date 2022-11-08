package com.mainsteam.stm.instancelib.bean;

import java.io.Serializable;
import java.util.HashSet;

public class ResourceInstanceCacheKey implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3511349384653731843L;
	
	private HashSet<Long> instanceIds;

	public ResourceInstanceCacheKey(){}
	
	public ResourceInstanceCacheKey(HashSet<Long> instanceIds){
		this.instanceIds = instanceIds;
	}
	
	public HashSet<Long> getInstanceIds() {
		return instanceIds;
	}

	public void setInstanceIds(HashSet<Long> instanceIds) {
		this.instanceIds = instanceIds;
	}
	
}
