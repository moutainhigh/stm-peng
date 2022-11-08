package com.mainsteam.stm.profile.fault.dao;

import java.util.List;

import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultInstance;

public interface ProfileFaultInstanceDao {

	/**
	* @Title: deleteProfileFaultInstance
	* @Description: 删除所有策略与资源实例的关系
	* @param profileId
	* @return  int
	* @throws
	*/
	int deleteProfileFaultInstance(long profileId);
	
	/**
	* @Title: batchInsertProfileFaultInstance
	* @Description: 批量添加策略与资源实例的关系
	* @param profileFaultInstances
	* @return  int
	* @throws
	*/
	int batchInsertProfileFaultInstance(List<ProfileFaultInstance> profileFaultInstances);
	
	/**
	* @Title: selectProfileFaultInstances
	* @Description: 通过策略ID查询策略关联的所有资源实例
	* @param profileId
	* @return  List<ProfileFaultInstance>
	* @throws
	*/
	List<ProfileFaultInstance> selectProfileFaultInstances(long profileId);
}
