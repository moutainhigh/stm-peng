package com.mainsteam.stm.portal.extendedplatform.resourceprofile.api;

import java.util.List;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.portal.extendedplatform.resourceprofile.po.ProfileInstancePo;

public interface InstanceService {

	List<ResourceInstance> queryInstanceById(long id);

	List<ProfileInstancePo> queryProfileInstancePos(long instanceId);

	List<ProfileInstancePo> queryProfileInstanceLastPos(long instanceId);
	
	/**
	* @Title: deleteProfileInstanceRel
	* @Description: 删除资源与策略的关系
	* @param profileId
	* @return  int
	* @throws
	*/
	int deleteProfileInstanceRel(long instanceId);
	
	/**
	* @Title: deleteProfileInstanceLastRel
	* @Description: 查询资源与策略的历史关系
	* @param profileId
	* @return  int
	* @throws
	*/
	int deleteProfileInstanceLastRel(long instanceId);
	
	public int deleteInstance(long instanceId);
}
