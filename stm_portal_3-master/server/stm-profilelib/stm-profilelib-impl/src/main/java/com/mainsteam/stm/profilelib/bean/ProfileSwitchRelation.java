package com.mainsteam.stm.profilelib.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

public class ProfileSwitchRelation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7409324501573746056L;

	/**
	 * 删除之前所有的资源对应的策略
	 * key:   instanceId
	 * value: profileId
	 */
	private HashMap<Long, Long> beforeDeletingAllRelation;
	
	/**
	 * 删除之前父资源对应的策略
	 * key:   instanceId
	 * value: profileId
	 */
	private HashMap<Long, Long> beforeDeletingParentRelation; 
	
	
	public HashMap<Long, Long> getBeforeDeletingAllRelation() {
		return beforeDeletingAllRelation;
	}

	public HashMap<Long, Long> getBeforeDeletingParentRelation() {
		return beforeDeletingParentRelation;
	}

	public void setBeforeDeletingAllRelation(
			HashMap<Long, Long> beforeDeletingAllRelation) {
		this.beforeDeletingAllRelation = beforeDeletingAllRelation;
	}

	public void setBeforeDeletingParentRelation(
			HashMap<Long, Long> beforeDeletingParentRelation) {
		this.beforeDeletingParentRelation = beforeDeletingParentRelation;
	}


	public ProfileSwitchRelation(HashMap<Long, Long> beforeDeletingAllRelation,HashMap<Long, Long> beforeDeletingParentRelation){
		this.beforeDeletingAllRelation = beforeDeletingAllRelation;
		this.beforeDeletingParentRelation = beforeDeletingParentRelation;
	}
	
	public ProfileSwitchRelation(){
	}

	public HashMap<Long, Boolean> calcIsSameProfile(HashMap<Long, Long> before,HashMap<Long, Long> after){
		HashMap<Long, Boolean> result = new HashMap<Long, Boolean>(after.size());
		if(before == null){
			//不算切换通知
			for (Entry<Long, Long> item : after.entrySet()) {
				result.put(item.getKey(), Boolean.TRUE);
			}
		}else{
			for (Entry<Long, Long> item : after.entrySet()) {
				Long profileId = before.get(item.getKey());
				if(profileId == null){
					//不算切换通知
					result.put(item.getKey(), Boolean.TRUE);
				}else{
					if(item.getValue().intValue() == profileId.longValue()){
						//不算切换通知
						result.put(item.getKey(), Boolean.TRUE);
					}else{
						//算切换通知
						result.put(item.getKey(), Boolean.FALSE);
					}
				}
			}
		}
		return result;
	}
	
}
