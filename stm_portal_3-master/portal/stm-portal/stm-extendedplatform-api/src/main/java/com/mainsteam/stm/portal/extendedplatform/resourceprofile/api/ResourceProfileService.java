package com.mainsteam.stm.portal.extendedplatform.resourceprofile.api;

import java.util.List;

import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.portal.extendedplatform.resourceprofile.po.CategoryPo;
import com.mainsteam.stm.portal.extendedplatform.resourceprofile.po.ProfileInstancePo;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.po.ProfileInfoPO;
import com.mainsteam.stm.profilelib.po.ProfileMetricPO;

public interface ResourceProfileService {

	/**
	* @Title: queryAllResourceDefs
	* @Description: 获取模型列表
	* @return  List<CategoryPo>
	* @throws
	*/
	public List<CategoryPo> queryAllResourceDefs();
	
	public List<CategoryPo> queryAllParentResource();
	
	/**
	* @Title: getResourceDef
	* @Description: 通过ID获取模型
	* @param id
	* @return  ResourceDef
	* @throws
	*/
	public ResourceDef getResourceDef(String id);
	
	/**
	* @Title: getAllParentProfiles
	* @Description: 获取所有主策略
	* @return  List<Profile>
	* @throws
	*/
	public List<ProfileInfoPO> getAllParentProfiles();
	
	
	/**
	* @Title: getMetricsByProfile
	* @Description: 通过策略ID获取指标ID
	* @param profileId
	* @return  List<ProfileMetric>
	* @throws
	*/
	public List<ProfileMetricPO> getMetricsByProfile(long profileId);
	
	/**
	* @Title: queryProfileInfoByResource
	* @Description: 通过ResourceID查询策略列表
	* @param resourceId
	* @return  List<ProfileInfoPO>
	* @throws
	*/
	public List<ProfileInfoPO> queryProfileInfoByResource(String resourceId);
	
	
	/**
	* @Title: removeProfileMetric
	* @Description: 通过ID删除策略指标数据
	* @param profileId 策略ID
	* @param metricId 指标ID
	* @return  boolean
	* @throws
	*/
	public boolean removeProfileMetric(long profileId,String metricId);
	
	
	/**
	* @Title: removeChildProfileById
	* @Description: 删除子策略，并同时删除策略相关指标、阈值、资源
	* @param profileId 子策略ＩＤ
	* @return  boolean
	* @throws
	*/
	public boolean removeChildProfileById(long profileId);
	
	/**
	* @Title: removeChildProfileById
	* @Description: 删除策略，并同时删除策略相关指标、阈值、资源
	* @param profileId 子策略ＩＤ
	* @return  boolean
	* @throws
	*/
	public boolean removeProfileByIds(List<Long> profileIds);
	
	/**
	* @Title: addProfileMetricByResource
	* @Description: 将模型中的指标添加到数据库策略指标表中
	* @param categoryId 模型ID
	* @param resourceId 资源ID
	* @param metricId 指标ID
	* @return  boolean
	* @throws
	*/
	public boolean addProfileMetricByResource(String resourceId, String metricId,long profileId);
	
	public List<ProfileInfoPO> queryProfileInfoById(long profileId);
	
	public List<ProfileInfoPO> queryProfilInfoByResourceId(String resourceId);
	
	public List<ProfileInstancePo> queryProfileInstanceRel(long profileId);
	
	public List<ProfileInstancePo> queryProfileInstanceLastRel(long profileId);
	
	public List<ProfileInstancePo> queryProfileInstanceRelByResourceId(String resourceId);
	
	public List<ProfileInstancePo> queryProfileInstanceLastRelByResourceId(String resourceId);
	
	
	int deleteProfileInstanceRel(long profileId);
	
	int deleteProfileInstanceLastRel(long profileId);
	
	int deleteProfileInstanceRelByResourceId(String resourceId);
	
	int deleteProfileInstanceLastRelByResourceId(String resourceId);
}
