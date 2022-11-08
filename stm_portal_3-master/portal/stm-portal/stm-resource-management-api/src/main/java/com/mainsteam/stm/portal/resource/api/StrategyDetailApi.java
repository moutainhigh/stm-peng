package com.mainsteam.stm.portal.resource.api;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.bo.PortalThreshold;
import com.mainsteam.stm.portal.resource.bo.zTreeBo;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.profilelib.obj.Threshold;
import com.mainsteam.stm.profilelib.objenum.ProfileTypeEnum;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;


/**
 * <li>文件名称: ResourceCategoryApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年7月28日
 * @author   pengl
 */
public interface StrategyDetailApi {
	
	/**
	 * 根据策略ID获取策略
	 * @return
	 */
	Profile getProfilesById(long profileId);
	
	/**
	 * 根据资源实例ID获取资源实例
	 */
	List<ResourceInstance> getResourceInstancesByIds(String ids,Long domainId,ILoginUser user);
	
	/**
	 * 根据子资源实例ID获取资源实例
	 */
	List<ResourceInstance> getChildResourceInstancesByIds(String ids,Long domainId,ILoginUser user);
	
	/**
	 * 根据资源实例ID获取其余资源实例
	 */
	List<ResourceInstanceBo> getExceptResourceInstancesByIds(String ids,String resourceId,Integer state,Long domainId,String searchContent,ILoginUser user);
	
	/**
	 * 根据子资源实例ID获取其余资源实例
	 */
	List<ResourceInstance> getExceptChildResourceInstancesByIds(String ids,String resourceId,Long mainProfileId,Long domainId,ILoginUser user);
	
	/**
	 * 构建树
	 * @return
	 */
	List<zTreeBo> getZTreeBoListByChildResourceInstance(List<ResourceInstance> childResourceInstance,String interfaceState);
	
	/**
	 * 删除子策略
	 */
	boolean removeChildProfileByIds(long profileId);
	
	/**
	 * 批量删除子策略
	 */
	boolean removeChildProfileByIdList(List<Long> profileId);
	
	/**
	 * 获取所有主策略
	 */
	List<ProfileInfo> getAllDefautProfileInfo(Long resourceId);
	
	/**
	 * 根据主资源ID获取子策略类型
	 */
	List<ResourceDef> getChildProfileTypeByResourceId(String resourceId);
	
	/**
	 * 添加子资源
	 */
	boolean addChildProfile(long profileParentId,ProfileInfo childProfileInfo,ProfileTypeEnum pt,ProfileInfo parentInfo);
	
	/**
	 * 修改策略基础信息
	 */
	boolean updateProfileBasicInfo(ProfileInfo info);
	
	/**
	 * 添加策略的资源
	 */
	boolean addProfileResource(long profileId,List<Long> instances);
	
	/**
	 * 删除策略的资源
	 */
	boolean reduceProfileResource(long profileId,List<Long> instances);
	
	/**
	 * 同时添加删除策略的资源
	 */
	boolean addAndDeleteProfileResource(long profileId,List<Long> addInstances,List<Long> reduceInstances,ProfileInfo basicInfo);
	
	/**
	 * 批量修改指标监控状态
	 */
	boolean updateProfileMetricMonitor(long profileId, Map<String, Boolean> monitor,ProfileInfo info);
	
	/**
	 * 批量修改指标告警状态
	 */
	boolean updateProfileMetricAlarm(long profileId, Map<String, Boolean> alarms,ProfileInfo info);
	
	/**
	 * 批量修改指标频度
	 */
	boolean updateProfileMetricFrequency(long profileId,Map<String, String> frequencyValue,ProfileInfo info);
	
	/**
	 * 批量修改指标阈值
	 */
	boolean updateProfileMetricThreshold(List<PortalThreshold> thresholds,ProfileInfo info);
	
	/**
	 * 批量修改指标告警重复次数
	 */
	boolean updateProfileMetricAlarmFlapping(long profileId,Map<String, Integer> flappingValue,ProfileInfo info);
	
	/**
	 * 根据资源实例ID获取当前策略信息
	 */
	ProfileInfo getProfileInfoByResourceId(long resourceId);
	
	/**
	 * 修改策略对象里的子策略的子资源实例关系
	 */
	long modifyChildProfileResourceRelation(Profile profile,ILoginUser user);
	
	/**
	 * 将指定资源实例添加进默认策略
	 */
	boolean addProfileIntoDefult(Long resourceInstanceId,ProfileInfo info);
	
	/**
	 * 将指定资源实例添加进自定义策略
	 */
	boolean addProfileIntoSpecial(Long resourceInstanceId,Long profileId,ProfileInfo info);
	
	/**
	 * 添加一个个性化策略并加入监控
	 */
	long addPersonalizeProfile(Profile profile,ILoginUser user);
	
	/**
	 * 添加指定实例到之前个性化策略
	 */
	boolean addInstanceIntoHistoryPersonalProfile(Long profileId,Long instanceId,ProfileInfo info);
	
	/**
	 * 采集接口状态
	 * @param mainInstanceId
	 * @param type
	 * @return
	 */
	List<MetricData> getInterfaceState(long mainInstanceId,String type);

	List<ResourceInstanceBo> getMainProfileResourceInstances(
			String resourceId, Integer state, Long domainId,
			String searchContent, ILoginUser user);

	/**
	 * 通过主资源实例得到其子资源实例列表
	 */
	List<ResourceInstance> getChildResourceInstancesByParentId(
			Long mainInstanceId, String resourceId, Long domainId,
			ILoginUser user,String interfaceState,String searchContent);
	
	/**
	 * 通过主资源实例过滤名称得到其子资源实例列表
	 */
	List<ResourceInstance> getChildResourceInstancesByResourceName(
			Long mainInstanceId, String resourceId, Long domainId,
			ILoginUser user,String interfaceState,String searchContent,String resourceName);
}
