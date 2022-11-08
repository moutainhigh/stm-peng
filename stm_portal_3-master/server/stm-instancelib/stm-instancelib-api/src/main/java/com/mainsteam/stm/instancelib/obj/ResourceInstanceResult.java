package com.mainsteam.stm.instancelib.obj;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;

public class ResourceInstanceResult {

	private long resourceInstanceId;
	
	private boolean isRepeat;
	
	List<Long> repeatIds;
	
	private Map<InstanceLifeStateEnum, List<Long>> instanceLiftStatte;
	
	public ResourceInstanceResult(long resourceInstanceId,boolean isRepeat,List<Long> repeatIds){
		this.isRepeat = isRepeat;
		this.resourceInstanceId = resourceInstanceId;
		this.repeatIds = repeatIds;
	}
	
	public ResourceInstanceResult(long resourceInstanceId,boolean isRepeat,List<Long> repeatIds, Map<InstanceLifeStateEnum, List<Long>> instanceLiftStatte){
		this.isRepeat = isRepeat;
		this.resourceInstanceId = resourceInstanceId;
		this.repeatIds = repeatIds;
		this.instanceLiftStatte=instanceLiftStatte;
	}
	
	public long getResourceInstanceId() {
		return resourceInstanceId;
	}

	public boolean isRepeat() {
		return isRepeat;
	}

	public void setResourceInstanceId(long resourceInstanceId) {
		this.resourceInstanceId = resourceInstanceId;
	}

	public void setRepeat(boolean isRepeat) {
		this.isRepeat = isRepeat;
	}

	public List<Long> getRepeatIds() {
		return repeatIds;
	}

	public void setRepeatIds(List<Long> repeatIds) {
		this.repeatIds = repeatIds;
	}
	
	public Map<InstanceLifeStateEnum, List<Long>> getInstanceLiftStatte() {
		return instanceLiftStatte;
	}
	public void setInstanceLiftStatte(Map<InstanceLifeStateEnum, List<Long>> instanceLiftStatte) {
		this.instanceLiftStatte = instanceLiftStatte;
	}
}
