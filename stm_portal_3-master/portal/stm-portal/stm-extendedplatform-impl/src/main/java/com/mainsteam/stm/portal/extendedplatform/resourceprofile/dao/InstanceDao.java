package com.mainsteam.stm.portal.extendedplatform.resourceprofile.dao;

import java.util.List;

import com.mainsteam.stm.instancelib.obj.InstanceProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.portal.extendedplatform.resourceprofile.po.ProfileInstancePo;

public interface InstanceDao {

	/**
	* @Title: queryInstanceById
	* @Description: 通过资源实例ID查询资源
	* @param id
	* @return  List<ResourceInstance>
	* @throws
	*/
	List<ResourceInstance> queryInstanceById(long id);
	
	/**
	* @Title: queryProfileInstancePos
	* @Description:查询资源与策略的关系
	* @param instanceId
	* @return  List<ProfileInstancePo>
	* @throws
	*/
	List<ProfileInstancePo> queryProfileInstancePos(long instanceId);
	
	/**
	* @Title: queryProfileInstanceLastPos
	* @Description: 查询资源与策略的历史关系
	* @param instanceId
	* @return  List<ProfileInstancePo>
	* @throws
	*/
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
	
	int deleteInstance(long instanceId);
}
