package com.mainsteam.stm.profile.fault.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultRelation;
import com.mainsteam.stm.profilelib.fault.obj.Profilefault;

public interface ProfileFaultDao {

	/**
	* @Title: pageSelect
	* @Description: 分页查询策略基本信息
	* @param page
	* @return  List<Profilefault>
	* @throws
	*/
	List<Profilefault> pageSelect(Page<Profilefault,Profilefault> page);
	
	/**
	* @Title: get
	* @Description:通过策略ID查询策略基本信息
	* @param profieId
	* @return  Profilefault
	* @throws
	*/
	ProfileFaultRelation get(long profieId);
	
	/**
	* @Title: insert
	* @Description: 插入策略基本信息
	* @param profilefault
	* @return  int
	* @throws
	*/
	int insert(Profilefault profilefault);
	
	/**
	* @Title: update
	* @Description: 更新策略基本信息
	* @param profilefault
	* @return  int
	* @throws
	*/
	int update(Profilefault profilefault);
	
	/**
	* @Title: update
	* @Description: 更新策略状态
	* @param profilefault
	* @return  int
	* @throws
	*/
	int updateState(long profileId);
	
	/**
	* @Title: batchDel
	* @Description: 批量删除策略基本信息
	* @param profileIds
	* @return  int
	* @throws
	*/
	int batchDel(Long[] profileIds);
	
	/**
	* @Title: queryProfilefaultByInstanceAndMetric
	* @Description: 通过资源实例ID和指标ID查询策略信息
	* @return  Profilefault
	* @throws
	*/
	Profilefault queryProfilefaultByInstanceAndMetric(String instanceId,String metricId);
}
