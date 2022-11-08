package com.mainsteam.stm.instancelib.service.bean;

import java.util.List;

import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;

/**
 * 资源实例验证后的结果实体类
 * @author xiaoruqiang
 */
public class ComparerResult {

	
	public ComparerResult(){
	}
	
	/**
	 * 实例化验证结果类
	 * @param resourceInstanceId 重复的资源实例Id
	 * @param isDelete   重复的资源实例是否是已经删除状态
	 */
	public ComparerResult(long resourceInstanceId,InstanceLifeStateEnum lifeState,List<Long> repeatIds){
		this.lifeState = lifeState;
		this.instanceId = resourceInstanceId;				
		this.repeatIds = repeatIds;
	}
	
	private InstanceLifeStateEnum lifeState;

	/**
	 * 资源实例Id
	 */
	private long instanceId;
	
	private List<Long> repeatIds;

	public long getInstanceId() {
		return instanceId;
	}


	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public InstanceLifeStateEnum getLifeState() {
		return lifeState;
	}

	public void setLifeState(InstanceLifeStateEnum lifeState) {
		this.lifeState = lifeState;
	}

	public List<Long> getRepeatIds() {
		return repeatIds;
	}

	public void setRepeatIds(List<Long> repeatIds) {
		this.repeatIds = repeatIds;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("instanceId:").append(this.instanceId).append(" ");
		str.append("lifeState:").append(lifeState);
		return str.toString();
	}
}
