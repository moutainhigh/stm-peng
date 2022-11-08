package com.mainsteam.stm.portal.resource.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.alarm.event.AlarmEventTemplateService;
import com.mainsteam.stm.alarm.obj.AlarmEventTemplate;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.dict.PerfMetricStateEnum;
import com.mainsteam.stm.caplib.dict.ResourceTypeConsts;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.resource.api.IAlarmProfileQueryApi;
import com.mainsteam.stm.portal.resource.api.StrategyDetailApi;
import com.mainsteam.stm.portal.resource.bo.PortalThreshold;
import com.mainsteam.stm.portal.resource.bo.zTreeBo;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.TimelineService;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.Instance;
import com.mainsteam.stm.profilelib.obj.MetricSetting;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.profilelib.obj.ProfileInstanceRelation;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.Threshold;
import com.mainsteam.stm.profilelib.objenum.ProfileTypeEnum;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.resource.bo.ResourceQueryBo;
import com.mainsteam.stm.util.ResourceOrMetricConst;
import com.mainsteam.stm.util.StringUtil;

public class StrategyDetailImpl implements StrategyDetailApi {

	@Resource
	private ProfileService profileService;
	
	@Resource
	private CapacityService capacityService;
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private IResourceApi stm_system_resourceApi;
	
	@Resource
	private IAlarmProfileQueryApi alarmProfileQueryApi;
	
	@Resource
	private MetricDataService metricDataService;
	
	@Resource
	private MetricStateService metricStateService;
	
	@Resource
	private ModulePropService modulePropService;
	
	@Resource
	private BizMainApi bizMainApi;
	
	@Resource
	private AlarmEventTemplateService alarmEventTemplateService;
	
	@Resource
	private TimelineService timelineService;
	
	private static final Log logger = LogFactory.getLog(StrategyDetailImpl.class);
	
	@Override
	public Profile getProfilesById(long profileId) {
		Profile proFile = null;
		try {
			proFile = profileService.getProfilesById(profileId);
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
		}
		return proFile;
		
	}
	
	/**
	 * 通过资源ID(主资源实例)得到资源实例列表
	 */
	@Override
	public List<ResourceInstance> getResourceInstancesByIds(String ids,Long domainId,ILoginUser user) {
		String[] resourceIdArray = ids.split(",");
		
		List<ResourceInstance> resourceAllList = new ArrayList<ResourceInstance>();
		
		List<Long> mainResourceInstanceIds = new ArrayList<Long>();
		
		//判断用户拥有哪些资源实例的权限
		for(int i = 0 ; i < resourceIdArray.length ; i ++){
			if(resourceIdArray[i].trim().equals("")){
				continue;
			}
			mainResourceInstanceIds.add(Long.parseLong(resourceIdArray[i]));
		}
		
		ResourceQueryBo queryBo = new ResourceQueryBo(user);
		List<Long> accessMainResourceIntancesIds = stm_system_resourceApi.accessFilter(queryBo, mainResourceInstanceIds);
		
		if(accessMainResourceIntancesIds == null || accessMainResourceIntancesIds.size() <= 0){
			return resourceAllList;
		}
		
		for(int i = 0 ; i < accessMainResourceIntancesIds.size() ; i ++){

			long resourceInstanceId = accessMainResourceIntancesIds.get(i);
			
			ResourceInstance resourceInstance = null;
			try {
				resourceInstance = resourceInstanceService.getResourceInstance(resourceInstanceId);
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage(),e);
				}
			}
			
			//判断自定义策略的域是否和资源实例相同
			if(domainId != -1 && resourceInstance.getDomainId() != domainId.longValue()){
				continue;
			}
			
			if(domainId == -1){
				Set<IDomain> domains = user.getDomains(ILoginUser.RIGHT_RESOURCE);
				boolean isContain = false;
				for(IDomain domain : domains){
					if(resourceInstance.getDomainId() == domain.getId()){
						isContain = true;
						break;
					}
				}
				
				if(!isContain){
					continue;
				}
				
			}
			
			if(resourceInstance.getLifeState() != InstanceLifeStateEnum.DELETED){
				resourceAllList.add(resourceInstance);
			}
			
		}
		return resourceAllList;
	}

	/**
	 * 通过资源ID(子资源实例)得到资源实例列表
	 */
	@Override
	public List<ResourceInstance> getChildResourceInstancesByIds(String ids,Long domainId,ILoginUser user) {
		String[] resourceIdArray = ids.split(",");
		
		List<ResourceInstance> resourceAllList = new ArrayList<ResourceInstance>();
		
		List<Long> mainResourceInstanceIds = new ArrayList<Long>();
		Set<Long> mainResourceInstanceSet = new HashSet<Long>();
		//判断用户拥有哪些资源实例的权限
		for(int i = 0 ; i < resourceIdArray.length ; i ++){
			if(resourceIdArray[i].trim().equals("")){
				continue;
			}
			try {
				ResourceInstance intance = resourceInstanceService.getResourceInstance(Long.parseLong(resourceIdArray[i]));
				if(intance == null){
					if(logger.isErrorEnabled()){
						logger.error("Get resourceinstance error,id : " + Long.parseLong(resourceIdArray[i]));
					}
					continue;
				}
				mainResourceInstanceSet.add(intance.getParentId());
			} catch (NumberFormatException e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage(),e);
				}
			} catch (InstancelibException e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage(),e);
				}
			}
		}
		
		mainResourceInstanceIds = new ArrayList<Long>(mainResourceInstanceSet);
		
		ResourceQueryBo queryBo = new ResourceQueryBo(user);
		List<Long> accessMainResourceIntancesIds = stm_system_resourceApi.accessFilter(queryBo, mainResourceInstanceIds);
		
		if(accessMainResourceIntancesIds == null || accessMainResourceIntancesIds.size() <= 0){
			return resourceAllList;
		}
		
		for(int i = 0 ; i < resourceIdArray.length ; i ++){

			long resourceInstanceId = Long.parseLong(resourceIdArray[i]);
			
			ResourceInstance resourceInstance = null;
			try {
				resourceInstance = resourceInstanceService.getResourceInstance(resourceInstanceId);
				if(resourceInstance == null){
					if(logger.isErrorEnabled()){
						logger.error("Get resourceinstance error,id : " + Long.parseLong(resourceIdArray[i]));
					}
					continue;
				}
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage(),e);
				}
			}
			
			if(!accessMainResourceIntancesIds.contains(resourceInstance.getParentId())){
				//没有该资源权限
				continue;
			}
			
			//判断自定义策略的域是否和资源实例相同
			if(domainId != -1 && resourceInstance.getDomainId() != domainId.longValue()){
				continue;
			}
			
			if(domainId == -1){
				Set<IDomain> domains = user.getDomains(ILoginUser.RIGHT_RESOURCE);
				boolean isContain = false;
				for(IDomain domain : domains){
					if(resourceInstance.getDomainId() == domain.getId()){
						isContain = true;
						break;
					}
				}
				
				if(!isContain){
					continue;
				}
				
			}
			
			if(resourceInstance.getLifeState() != InstanceLifeStateEnum.DELETED){
				resourceAllList.add(resourceInstance);
			}
			
		}
		return resourceAllList;
	}
	
	/**
	 * 通过主资源实例得到其余资源实例列表
	 */
	@Override
	public List<ResourceInstanceBo> getExceptResourceInstancesByIds(String ids,String resourceId,Integer state,Long domainId,String searchContent,ILoginUser user) {
		List<ResourceInstanceBo> resourceInstanceList = null;
		try {
			ResourceQueryBo queryBo = new ResourceQueryBo(user);
			queryBo.setModuleId(resourceId);
			resourceInstanceList = stm_system_resourceApi.getResources(queryBo);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
		}
		
		String idArray[] = ids.split(",");
		
		List<ResourceInstanceBo> resultResourceInstanceList = new ArrayList<ResourceInstanceBo>();
		
		if(resourceInstanceList == null || resourceInstanceList.size() <= 0){
			
			return resultResourceInstanceList;
			
		}
		listFor : for(int j = 0 ; j < resourceInstanceList.size() ; j ++){
			
			ResourceInstanceBo instanceBo = resourceInstanceList.get(j);
			
			if(instanceBo == null){
				continue;
			}
			
			//判断自定义策略的域是否和资源实例相同
			if(domainId != -1 && !instanceBo.getDomainId().equals(domainId)){
				continue;
			}
			
			if(domainId == -1){
				Set<IDomain> domains = user.getDomains(ILoginUser.RIGHT_RESOURCE);
				boolean isContain = false;
				for(IDomain domain : domains){
					if(resourceInstanceList.get(j).getDomainId().longValue() == domain.getId()){
						isContain = true;
						break;
					}
				}
				
				if(!isContain){
					continue;
				}
				
			}
			
			if(state == 0){
				//获取全部
				if(instanceBo.getLifeState() == InstanceLifeStateEnum.DELETED 
						|| instanceBo.getLifeState() == InstanceLifeStateEnum.INITIALIZE){
					continue;
				}
			}else if(state == 1){
				//获取已监控
				if(instanceBo.getLifeState() == InstanceLifeStateEnum.DELETED
						    || instanceBo.getLifeState() == InstanceLifeStateEnum.INITIALIZE
							|| instanceBo.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED){
					continue;
				}
			}else if(state == 2){
				//获取未监控
				if(instanceBo.getLifeState() == InstanceLifeStateEnum.DELETED
					    || instanceBo.getLifeState() == InstanceLifeStateEnum.INITIALIZE
						|| instanceBo.getLifeState() == InstanceLifeStateEnum.MONITORED){
					continue;
				}
			}
			
			//过滤搜索关键字
			if(searchContent != null && !searchContent.equals("")){
				if(instanceBo.getShowName() != null && 
						!instanceBo.getShowName().toLowerCase().contains(searchContent.toLowerCase()) && 
						!instanceBo.getDiscoverIP().toLowerCase().contains(searchContent.toLowerCase())){
					//不匹配关键字
					continue;
				}
			}
			
			for(int i = 0 ; i < idArray.length ; i ++){
				
				long idArrayItem = -1L;
				try {
					idArrayItem = Long.parseLong(idArray[i]);
				} catch (NumberFormatException e) {
					if(logger.isErrorEnabled()){
						logger.error(e.getMessage(),e);
					}
					continue;
				}
				
				if(idArrayItem == instanceBo.getId().longValue()){
					
					continue listFor;
					
				}
				
			}
				
			resultResourceInstanceList.add(resourceInstanceList.get(j));
			
		}
			
		return resultResourceInstanceList;
	}
	
	/**
	 * 通过主资源实例得到其余资源实例列表
	 */
	@Override
	public List<ResourceInstanceBo> getMainProfileResourceInstances(String resourceId,Integer state,Long domainId,String searchContent,ILoginUser user) {
		List<ResourceInstanceBo> resourceInstanceList = null;
		try {
			ResourceQueryBo queryBo = new ResourceQueryBo(user);
			queryBo.setModuleId(resourceId);
			resourceInstanceList = stm_system_resourceApi.getResources(queryBo);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
		}
		
		List<ResourceInstanceBo> resultResourceInstanceList = new ArrayList<ResourceInstanceBo>();
		
		if(resourceInstanceList == null || resourceInstanceList.size() <= 0){
			
			return resultResourceInstanceList;
			
		}
		for(int j = 0 ; j < resourceInstanceList.size() ; j ++){
			
			ResourceInstanceBo instanceBo = resourceInstanceList.get(j);
			
			if(instanceBo == null){
				continue;
			}
			
			//判断自定义策略的域是否和资源实例相同
			if(domainId != -1 && !instanceBo.getDomainId().equals(domainId)){
				continue;
			}
			
			if(domainId == -1){
				Set<IDomain> domains = user.getDomains(ILoginUser.RIGHT_RESOURCE);
				boolean isContain = false;
				for(IDomain domain : domains){
					if(resourceInstanceList.get(j).getDomainId().longValue() == domain.getId()){
						isContain = true;
						break;
					}
				}
				
				if(!isContain){
					continue;
				}
				
			}
			
			if(state == 0){
				//获取全部
				if(instanceBo.getLifeState() == InstanceLifeStateEnum.DELETED 
						|| instanceBo.getLifeState() == InstanceLifeStateEnum.INITIALIZE){
					continue;
				}
			}else if(state == 1){
				//获取已监控
				if(instanceBo.getLifeState() == InstanceLifeStateEnum.DELETED
						    || instanceBo.getLifeState() == InstanceLifeStateEnum.INITIALIZE
							|| instanceBo.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED){
					continue;
				}
			}else if(state == 2){
				//获取未监控
				if(instanceBo.getLifeState() == InstanceLifeStateEnum.DELETED
					    || instanceBo.getLifeState() == InstanceLifeStateEnum.INITIALIZE
						|| instanceBo.getLifeState() == InstanceLifeStateEnum.MONITORED){
					continue;
				}
			}
			
			//过滤搜索关键字
			if(searchContent != null && !searchContent.equals("")){
				boolean isContainName = false;
				boolean isContainIp = false;
				if(instanceBo.getShowName() != null && instanceBo.getShowName().toLowerCase().contains(searchContent.toLowerCase())){
					//匹配名字
					isContainName = true;
				}
				if(instanceBo.getDiscoverIP() != null && instanceBo.getDiscoverIP().toLowerCase().contains(searchContent.toLowerCase())){
					//匹配IP
					isContainIp = true;
				}
				if(!isContainName && !isContainIp){
					continue;
				}
			}
			
			resultResourceInstanceList.add(resourceInstanceList.get(j));
			
		}
			
		return resultResourceInstanceList;
	}
	
	/**
	 * 通过子资源实例得到其余资源实例列表
	 */
	@Override
	public List<ResourceInstance> getExceptChildResourceInstancesByIds(String ids,String resourceId,Long mainProfileId,Long domainId,ILoginUser user) {
		List<ResourceInstance> resourceChildInstanceList = new ArrayList<ResourceInstance>();
		try {
			
			//获取主策略的所有资源
			Profile profile = profileService.getProfilesById(mainProfileId);
			ProfileInstanceRelation relationsList = profile.getProfileInstanceRelations();
			List<Instance> intanceList = relationsList.getInstances();
			
			//查找子资源实例是否属于用户权限
			List<Long> mainResourceInstanceIds = new ArrayList<Long>();
			Set<Long> mainResourceInstanceSet = new HashSet<Long>();
			for(int i = 0 ; i < intanceList.size() ; i++){
				mainResourceInstanceSet.add(intanceList.get(i).getInstanceId());
			}
			
			mainResourceInstanceIds = new ArrayList<Long>(mainResourceInstanceSet);
			
			ResourceQueryBo queryBo = new ResourceQueryBo(user);
			List<Long> accessMainResourceIntancesIds = stm_system_resourceApi.accessFilter(queryBo, mainResourceInstanceIds);
			
			for(int i = 0 ; i < intanceList.size() ; i++){
				
				if(!accessMainResourceIntancesIds.contains(intanceList.get(i).getInstanceId())){
					//该用户没有该资源权限
					continue;
				}
				ResourceInstance resourceInstance = resourceInstanceService.getResourceInstance(intanceList.get(i).getInstanceId());
				
				if(resourceInstance == null){
					continue;
				}
				
				if(resourceInstance.getChildren() == null || resourceInstance.getChildren().size() <= 0){
					continue;
				}
				
				resourceChildInstanceList.addAll(resourceInstance.getChildren());
				
			}
			
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
		}
		
		String idArray[] = ids.split(",");
		
		List<ResourceInstance> resultResourceInstanceList = new ArrayList<ResourceInstance>();
		
		listFor : for(int j = 0 ; j < resourceChildInstanceList.size() ; j ++){
			
			String curResourceId = resourceChildInstanceList.get(j).getResourceId();
			
			//判断自定义策略的域是否和资源实例相同
			if(domainId != -1 && resourceChildInstanceList.get(j).getDomainId() != domainId.longValue()){
				continue;
			}
			
			if(domainId == -1){
				Set<IDomain> domains = user.getDomains(ILoginUser.RIGHT_RESOURCE);
				boolean isContain = false;
				for(IDomain domain : domains){
					if(resourceChildInstanceList.get(j).getDomainId() == domain.getId()){
						isContain = true;
						break;
					}
				}
				
				if(!isContain){
					continue;
				}
				
			}
			
			if(!curResourceId.equals(resourceId) || resourceChildInstanceList.get(j).getLifeState() == InstanceLifeStateEnum.DELETED
					|| resourceChildInstanceList.get(j).getLifeState() == InstanceLifeStateEnum.INITIALIZE){
				continue;
			}
			for(int i = 0 ; i < idArray.length ; i ++){
				
				long idArrayItem = -1L;
				try {
					if(null==idArray[i] || "".equals(idArray[i])) break;
						
					idArrayItem = Long.parseLong(idArray[i]);
				} catch (NumberFormatException e) {
					if(logger.isErrorEnabled()){
						logger.error(e.getMessage(),e);
					}
					continue;
				}
				
				if(idArrayItem == resourceChildInstanceList.get(j).getId()){
					
					continue listFor;
					
				}
				
			}
				
			resultResourceInstanceList.add(resourceChildInstanceList.get(j));
			
		}
		
		return resultResourceInstanceList;
		
	}
	
	/**
	 * 通过主资源实例得到其子资源实例列表
	 */
	@Override
	public List<ResourceInstance> getChildResourceInstancesByParentId(Long mainInstanceId,String resourceId,
			Long domainId,ILoginUser user,String interfaceState,String searchContent) {
		List<ResourceInstance> resourceChildInstanceList = new ArrayList<ResourceInstance>();

		ResourceInstance resourceInstance = null;
		try {
			resourceInstance = resourceInstanceService.getResourceInstance(mainInstanceId);
		} catch (InstancelibException e1) {
			logger.error(e1.getMessage(),e1);
		}
		
		if(resourceInstance == null){
			return resourceChildInstanceList;
		}
		
		if(resourceInstance.getChildren() == null || resourceInstance.getChildren().size() <= 0){
			return resourceChildInstanceList;
		}
		
		resourceChildInstanceList.addAll(resourceInstance.getChildren());
		
		List<ResourceInstance> resultResourceInstanceList = new ArrayList<ResourceInstance>();
		
		for(int j = 0 ; j < resourceChildInstanceList.size() ; j ++){
			
			String curResourceId = resourceChildInstanceList.get(j).getResourceId();
			
			//判断自定义策略的域是否和资源实例相同
			if(domainId != -1 && resourceChildInstanceList.get(j).getDomainId() != domainId.longValue()){
				continue;
			}
			
			if(domainId == -1){
				Set<IDomain> domains = user.getDomains(ILoginUser.RIGHT_RESOURCE);
				boolean isContain = false;
				for(IDomain domain : domains){
					if(resourceChildInstanceList.get(j).getDomainId() == domain.getId()){
						isContain = true;
						break;
					}
				}
				
				if(!isContain){
					continue;
				}
				
			}
			
			if(!curResourceId.equals(resourceId) || resourceChildInstanceList.get(j).getLifeState() == InstanceLifeStateEnum.DELETED
					|| resourceChildInstanceList.get(j).getLifeState() == InstanceLifeStateEnum.INITIALIZE){
				continue;
			}
			
			
			if(interfaceState != null && !(interfaceState.equals(""))){
				//根据接口状态构建资源
				
				//监控的资源查看采集数据，未监控的查看资源模型属性
				if(resourceChildInstanceList.get(j).getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
					MetricStateData data = metricStateService.getMetricState(resourceChildInstanceList.get(j).getId(), ResourceOrMetricConst.NETWORK_INTERFACE_AVAILABILITY_METRICID);
					if("up".equals(interfaceState)) {//"up"只添加接口状态为一的资源
						if (data != null) {
							if (!MetricStateEnum.NORMAL.equals(data.getState())) {
								continue;
							}
						}
					} else if ("down".equals(interfaceState)) {
						if (data != null) {
							if (!MetricStateEnum.CRITICAL.equals(data.getState())) {
								continue;
							}
						}
					}
				}else{
					String[] availableArr = resourceChildInstanceList.get(j).getModulePropBykey(ResourceOrMetricConst.RESOURCE_AVAILABILITY);
					if("up".equals(interfaceState)) {//"up"只添加接口状态为一的资源
						if (availableArr != null && availableArr.length > 0) {
							if (!"1".equals(availableArr[0])) {
								continue;
							}
						}
					} else if ("down".equals(interfaceState)) {
						if (availableArr != null && availableArr.length > 0) {
							if (!"0".equals(availableArr[0])) {
								continue;
							}
						}
					}
				}
			}
			
			//过滤搜索关键字
			if(searchContent != null && !searchContent.equals("")){
				if(resourceChildInstanceList.get(j).getShowName() != null && !resourceChildInstanceList.get(j).getShowName().toLowerCase().contains(searchContent.toLowerCase())){
					//不匹配关键字
					continue;
				}else if(resourceChildInstanceList.get(j).getShowName() == null && resourceChildInstanceList.get(j).getName() != null && !resourceChildInstanceList.get(j).getName().toLowerCase().contains(searchContent.toLowerCase())){
					//不匹配关键字
					continue;
				}
				
			}
				
			resultResourceInstanceList.add(resourceChildInstanceList.get(j));
			
		}
		
		return resultResourceInstanceList;
		
	}
	
	public List<zTreeBo> getZTreeBoListByChildResourceInstance(List<ResourceInstance> childResourceInstance,String interfaceState){
		
		List<zTreeBo> treeTwoList = new ArrayList<zTreeBo>();
		
		Map<String, List<zTreeBo>> secondTreeAndInstanceMap = new HashMap<String, List<zTreeBo>>();
		
		
		for(int i = 0 ; i < childResourceInstance.size() ; i ++){
			
			ResourceInstance resourceInstance = childResourceInstance.get(i);
			
			//根据接口状态构建资源
			//监控的资源查看采集数据，未监控的查看资源模型属性
			if(resourceInstance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
				MetricStateData data = metricStateService.getMetricState(resourceInstance.getId(), ResourceOrMetricConst.NETWORK_INTERFACE_AVAILABILITY_METRICID);
				if("up".equals(interfaceState)) {//"up"只添加接口状态为一的资源
					if (data != null) {
						if (!MetricStateEnum.NORMAL.equals(data.getState())) {
							continue;
						}
					}
				} else if ("down".equals(interfaceState)) {
					if (data != null) {
						if (!MetricStateEnum.CRITICAL.equals(data.getState())) {
							continue;
						}
					}
				}
			}else{
				
				String[] availableArr = resourceInstance.getModulePropBykey(ResourceOrMetricConst.RESOURCE_AVAILABILITY);
				if("up".equals(interfaceState)) {//"up"只添加接口状态为一的资源
					if (availableArr != null && availableArr.length > 0) {
						if (!"1".equals(availableArr[0])) {
							continue;
						}
					}
				} else if ("down".equals(interfaceState)) {
					if (availableArr != null && availableArr.length > 0) {
						if (!"0".equals(availableArr[0])) {
							continue;
						}
					}
				}
			}
			
			//根据资源实例ID获取二级类别
			ResourceInstance parentResourceInstance = null;
			try {
				parentResourceInstance = resourceInstanceService.getResourceInstance(resourceInstance.getParentId());
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage(),e);
				}
			}
			
			if(parentResourceInstance == null){
				if(logger.isErrorEnabled()){
					logger.error("Get parentResourceInstance is null , id : " + resourceInstance.getParentId());
				}
				continue;
			}
			
			zTreeBo instanceTree = this.defTozTreeVo(resourceInstance, false, parentResourceInstance.getId() + "");
			
			zTreeBo secondTree = this.defTozTreeVo(parentResourceInstance, true, "0");
			
			//判断secondTreeAndInstanceMap中是否存在二级类别ID的数据
			if(!secondTreeAndInstanceMap.containsKey(secondTree.getId())){
				
				secondTreeAndInstanceMap.put(secondTree.getId(), new ArrayList<zTreeBo>());
				
			}
			
			secondTreeAndInstanceMap.get(secondTree.getId()).add(instanceTree);
			
			if(!treeTwoList.contains(secondTree)){
				
				treeTwoList.add(secondTree);
				
			}
			
		}
		
		//构建二级树
		for(zTreeBo secondTree : treeTwoList){
			
			secondTree.setChildren(secondTreeAndInstanceMap.get(secondTree.getId()));
			
		}
		return treeTwoList;
		
	}
	
	private zTreeBo defTozTreeVo(ResourceInstance instance,boolean isParent,String pid){
		zTreeBo tree = new zTreeBo();
		if (!isParent) {
			String available = "";
			if(instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
				MetricStateData data = metricStateService.getMetricState(instance.getId(), ResourceOrMetricConst.NETWORK_INTERFACE_AVAILABILITY_METRICID);
				if (data != null) {
					if(data.getState().equals(MetricStateEnum.CRITICAL)){
						available = "0";
					}else{
						available = "1";
					}
				}
			}else{
				String[] availableArr = instance.getModulePropBykey(ResourceOrMetricConst.RESOURCE_AVAILABILITY);
				if (availableArr != null && availableArr.length > 0) {
					available = availableArr[0];
				}
			}
			Map<String, String> extendAttribute = new HashMap<String,String>();
			extendAttribute.put("available", available);
			tree.setExtendAttribute(extendAttribute);
		}
		tree.setId(instance.getId() + "");
		tree.setIsParent(isParent);
		tree.setName(instance.getShowName() == null ? instance.getName() : instance.getShowName());
		tree.setNocheck(false);
		tree.setOpen(false);
		tree.setPId(pid);
		return tree;
			
			
		
	}

	@Override
	/**
	 * 删除子策略
	 */
	public boolean removeChildProfileByIds(long profileId) {
		
		try {
			profileService.removeProfileById(profileId);
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
			return false;
		}
		return true;
		
	}
	
	@Override
	/**
	 * 删除子策略
	 */
	public boolean removeChildProfileByIdList(List<Long> profileList) {
		
		try {
			profileService.removeProfileByIds(profileList);
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
			return false;
		}
		return true;
		
	}

	@Override
	/**
	 * 获取所有默认主资源
	 */
	public List<ProfileInfo> getAllDefautProfileInfo(Long resourceId) {
		List<ProfileInfo> infoList = new ArrayList<ProfileInfo>();
		List<ProfileInfo> resultList = new ArrayList<ProfileInfo>();
		try {
			//根据资源实例ID查找当前策略
			boolean curProfileIsInDefaultOrSpecial = false;
			//获取是否有个性化策略
			ProfileInfo historyPersonalProfile = profileService.getPersonalizeProfileBasicInfoByResourceInstanceId(resourceId);
			ProfileInfo curProfile = profileService.getBasicInfoByResourceInstanceId(resourceId);
			infoList = profileService.getParentProfileBasicInfoByType(ProfileTypeEnum.DEFAULT);
			infoList.addAll(profileService.getParentProfileBasicInfoByType(ProfileTypeEnum.SPECIAL));
			ResourceInstance instance = resourceInstanceService.getResourceInstance(resourceId);
			String resourceModelId = instance.getResourceId();
			long instanceDomainId = instance.getDomainId();
			for(ProfileInfo info : infoList){
				if(info.getProfileId() == curProfile.getProfileId()){
					//当前策略在自定义和默认中
					resultList.add(0, info);
					curProfileIsInDefaultOrSpecial = true;
					continue;
				}
				if((info.getResourceId().equals(resourceModelId) && instanceDomainId == info.getDomainId() && info.getProfileType() == ProfileTypeEnum.SPECIAL) ||
						(info.getResourceId().equals(resourceModelId) && info.getProfileType() == ProfileTypeEnum.DEFAULT)){
					resultList.add(info);
				}
			}
			
			if(!curProfileIsInDefaultOrSpecial){
				resultList.add(0, curProfile);
			}else{
				if(historyPersonalProfile != null){
					resultList.add(1, historyPersonalProfile);
				}
			}
			
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
		} catch (InstancelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
		}
		return resultList;
	}

	@Override
	/**
	 * 根据主资源ID获取子策略类型
	 */
	public List<ResourceDef> getChildProfileTypeByResourceId(String resourceId) {
		List<ResourceDef> defList = new ArrayList<ResourceDef>();
		
		ResourceDef def = capacityService.getResourceDefById(resourceId);
		ResourceDef[] childDef = def.getChildResourceDefs();
		
		if(childDef == null || childDef.length <= 0){
			return defList;
		}
		
		
		for(int i = 0 ; i < childDef.length ; i ++){
			
			ResourceDef result = new ResourceDef();
			result.setId(childDef[i].getId());
			result.setName(childDef[i].getName());
			
			defList.add(result);
			
		}
		return defList;
		
	}

	@Override
	/**
	 * 添加子策略
	 */
	public boolean addChildProfile(long profileParentId,ProfileInfo childProfileInfo,ProfileTypeEnum pt,ProfileInfo parentInfo) {
		childProfileInfo.setProfileType(pt);
		
		try {
			profileService.createChildProfile(profileParentId, childProfileInfo);
			profileService.updateProfileBaiscInfo(parentInfo);
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
			return false;
		}
		return true;
		
	}

	@Override
	/**
	 * 修改策略基础信息
	 */
	public boolean updateProfileBasicInfo(ProfileInfo info) {
		try {
			profileService.updateProfileBaiscInfo(info);
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
			return false;
		}
		return true;
	}

	@Override
	/**
	 * 添加策略的资源
	 */
	public boolean addProfileResource(long profileId, List<Long> instances) {
		try {
			profileService.addProfileInstance(profileId, instances);
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
			return false;
		}
		return true;
		
	}

	@Override
	/**
	 * 移出策略的资源
	 */
	public boolean reduceProfileResource(long profileId, List<Long> instances) {
		try {
			profileService.removeInstancesFromProfile(profileId, instances);
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean addAndDeleteProfileResource(long profileId,
			List<Long> addInstances, List<Long> reduceInstances,ProfileInfo basicInfo) {
		try {
			profileService.operateProfileInstance(profileId, addInstances, reduceInstances);
			profileService.updateProfileBaiscInfo(basicInfo);
			
			List<Long> changeList = new ArrayList<Long>();
			if(addInstances != null && addInstances.size() > 0){
				changeList.addAll(addInstances);
			}
			if(reduceInstances != null && reduceInstances.size() > 0){
				changeList.addAll(reduceInstances);
			}
			if(changeList != null && changeList.size() > 0){
				bizMainApi.instanceMonitorChangeBiz(changeList);
			}
			
			List<Long> newProfileInstance = new ArrayList<Long>();
			Set<Long> profileInstanceSet = new HashSet<Long>();
			Profile nowProfile = profileService.getProfilesById(profileId);
			if(nowProfile.getProfileInfo() != null && nowProfile.getProfileInfo().getParentProfileId() > 0){
				if(nowProfile.getProfileInstanceRelations() != null && nowProfile.getProfileInstanceRelations().getInstances() != null 
						&& nowProfile.getProfileInstanceRelations().getInstances().size() > 0){
					
					List<Instance> instances = nowProfile.getProfileInstanceRelations().getInstances();
					for(Instance instance : instances){
						try {
							ResourceInstance resourceInstance = resourceInstanceService.getResourceInstance(instance.getInstanceId());
							if(resourceInstance == null){
								continue;
							}
							profileInstanceSet.add(resourceInstance.getDomainId());
						} catch (InstancelibException e) {
							logger.error(e.getMessage(),e);
						}
						
					}
					newProfileInstance = new ArrayList<Long>(profileInstanceSet);
				}
				
				alarmProfileQueryApi.filterUserByResourceDomainIdList(newProfileInstance, profileId);
			}
			
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
			return false;
		}
		return true;
	}

	@Override
	/**
	 * 批量修改指标监控状态
	 */
	public boolean updateProfileMetricMonitor(long profileId,
			Map<String, Boolean> monitor,ProfileInfo info) {
		try {
			profileService.updateProfileMetricMonitor(profileId, monitor);
			profileService.updateProfileBaiscInfo(info);
			List<String> metrics = new ArrayList<String>();
			for(String metricId : monitor.keySet()){
				metrics.add(metricId);
			}
			bizMainApi.metricMonitorOrAlarmChange(profileId, metrics);
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
			return false;
		}
		return true;
	}

	@Override
	/**
	 * 批量修改指标告警状态
	 */
	public boolean updateProfileMetricAlarm(long profileId,
			Map<String, Boolean> alarms,ProfileInfo info) {
		try {
			profileService.updateProfileMetricAlarm(profileId, alarms);
			profileService.updateProfileBaiscInfo(info);
			List<String> metrics = new ArrayList<String>();
			for(String metricId : alarms.keySet()){
				metrics.add(metricId);
			}
			bizMainApi.metricMonitorOrAlarmChange(profileId, metrics);
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
			return false;
		}
		return true;
	}

	@Override
	/**
	 * 批量修改指标频度
	 */
	public boolean updateProfileMetricFrequency(long profileId,
			Map<String, String> frequencyValue,ProfileInfo info) {
		try {
			profileService.updateProfileMetricFrequency(profileId, frequencyValue);
			profileService.updateProfileBaiscInfo(info);
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
			return false;
		}
		return true;
	}

	@Override
	/**
	 * 批量修改指标阈值
	 */
	public boolean updateProfileMetricThreshold(List<PortalThreshold> thresholds,ProfileInfo info) {
		try {
			
			List<Threshold> updateThresholds = new ArrayList<Threshold>();
			
			Map<InstanceStateEnum, String> content = new HashMap<InstanceStateEnum, String>();
			
			String oldAlarmTemplateId = null;
			String metricId = null;
			long timeLineId = 0;
			
			for(PortalThreshold threshold : thresholds){
				
				if(threshold.getPerfMetricStateEnum().equals(PerfMetricStateEnum.Normal)){
					content.put(InstanceStateEnum.NORMAL, threshold.getAlarmContent());
				}else if(threshold.getPerfMetricStateEnum().equals(PerfMetricStateEnum.Minor)){
					content.put(InstanceStateEnum.WARN, threshold.getAlarmContent());
				}else if(threshold.getPerfMetricStateEnum().equals(PerfMetricStateEnum.Major)){
					content.put(InstanceStateEnum.SERIOUS, threshold.getAlarmContent());
				}
				
				oldAlarmTemplateId = threshold.getAlarmTemplate();
				metricId = threshold.getMetricId();
				timeLineId = threshold.getTimelineId();
				
			}
			
			AlarmEventTemplate alarmEvent = new AlarmEventTemplate();
			
			alarmEvent.setContent(content);
			if(oldAlarmTemplateId != null && !oldAlarmTemplateId.equals("")){
				alarmEvent.setUniqueKey(oldAlarmTemplateId);
			}
			alarmEvent.setModuleType(SysModuleEnum.MONITOR);
			alarmEvent.setProfileId(info.getProfileId());
			alarmEvent.setMetricId(metricId);
			if(timeLineId > 0){
				alarmEvent.setTimelineId(timeLineId);
			}
			try {
				alarmEvent.setMainProfile(profileService.getProfilesById(info.getProfileId()).getProfileInfo().getParentProfileId() > 0 ? false : true);
			} catch (ProfilelibException e) {
				logger.error(e.getMessage(),e);
			}
			
			String newAlarmTemplateId = alarmEventTemplateService.updateTemplate(alarmEvent);
			
			for(PortalThreshold threshold : thresholds){
				
				Threshold updateThreshold = new Threshold();
				
				updateThreshold.setThreshold_mkId(threshold.getThreshold_mkId());
				updateThreshold.setThresholdExpression(threshold.getThresholdExpression());
				updateThreshold.setAlarmTemplate(newAlarmTemplateId);
				
				updateThresholds.add(updateThreshold);
				
			}
			
			if(timeLineId > 0){
				timelineService.updateTimelineMetricThreshold(timeLineId, updateThresholds);
			}else{
				profileService.updateProfileMetricThreshold(info.getProfileId(),updateThresholds);
			}
			
			profileService.updateProfileBaiscInfo(info);
			
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
			return false;
		}
		return true;
	}

	@Override
	/**
	 * 批量修改指标告警重复次数
	 */
	public boolean updateProfileMetricAlarmFlapping(long profileId,
			Map<String, Integer> flappingValue,ProfileInfo info) {
		try {
			profileService.updateProfileBaiscInfo(info);
			profileService.updateProfileMetricflapping(profileId, flappingValue);
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
			return false;
		}
		return true;
	}
	
	/**
	 * 通过资源实例ID查询当前策略
	 */
	@Override
	public ProfileInfo getProfileInfoByResourceId(long resourceId) {
		ProfileInfo info = null;
		try {
			info = profileService.getBasicInfoByResourceInstanceId(resourceId);
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
		}
		return info;
	}

	/**
	 * 修改策略对象里的子策略的子资源实例关系和添加信息指标
	 */
	@Override
	public long modifyChildProfileResourceRelation(Profile profile,ILoginUser user) {
		//添加创建人
		profile.getProfileInfo().setCreateUser(user.getId());
		
//		List<Profile> childProfileList = profile.getChildren();
		
		long mainResourceId = profile.getProfileInstanceRelations().getInstances().get(0).getInstanceId();
		long oldProfileId = -1;
		try {
			ProfileInfo oldInfo = profileService.getBasicInfoByResourceInstanceId(mainResourceId);
			if(oldInfo == null){
				if(logger.isErrorEnabled()){
					logger.error("Get oldInfo error ,resourceId : " + mainResourceId);
				}
			}
			oldProfileId = oldInfo != null ? oldInfo.getProfileId() : null;
		} catch (ProfilelibException e1) {
			if(logger.isErrorEnabled()){
				logger.error(e1.getMessage(),e1);
			}
		}
		
		//添加信息指标
		Profile oldProfile = null;
		try {
			oldProfile = profileService.getEmptyPersonalizeProfile(profile.getProfileInfo().getResourceId(), mainResourceId);
			if(oldProfile == null || oldProfile.getMetricSetting() == null || oldProfile.getMetricSetting().getMetrics() == null ||
					oldProfile.getMetricSetting().getMetrics().size() <= 0){
				if(logger.isErrorEnabled()){
					logger.error("Get oldProfile error , profileId : " + oldProfileId);
				}
				return oldProfileId;
			}
			
//			modifyMetricSetting(oldProfile,profile);
			
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
		}
		
//		if(childProfileList == null || childProfileList.size() <= 0){
//			return oldProfileId;
//		}
//		
//		for(int i = 0 ; i < childProfileList.size() ; i ++){
//			
//			//添加子策略的信息指标
//			modifyMetricSetting(oldProfile.getChildren().get(i),profile.getChildren().get(i));
//		}
		
		return oldProfileId;
	}
	
	//修改个性化策略的信息指标
	private void modifyMetricSetting(Profile oldProfile,Profile newProfile){
		
		List<ProfileMetric> metircList = oldProfile.getMetricSetting().getMetrics();
		
		String resourceId = oldProfile.getProfileInfo().getResourceId();
		
		if(resourceId == null || resourceId.equals("")){
			if(logger.isErrorEnabled()){
				logger.error("Get oldProfile resourceId error");
			}
			return;
		}
		
		if(newProfile.getMetricSetting() == null){
			newProfile.setMetricSetting(new MetricSetting());
		}
		
		if(newProfile.getMetricSetting().getMetrics() == null){
			newProfile.getMetricSetting().setMetrics(new ArrayList<ProfileMetric>());
		}
		
		for(ProfileMetric metric : metircList){
			ResourceMetricDef metricDef = capacityService.getResourceMetricDef(resourceId, metric.getMetricId());
			if(metricDef == null){
				if(logger.isErrorEnabled()){
					logger.error("Get metricDef error , resourceId : " + resourceId + ",metricId : " + metric.getMetricId());
				}
				continue;
			}
			if(metricDef.getMetricType() == MetricTypeEnum.InformationMetric){
				//信息指标
				newProfile.getMetricSetting().getMetrics().add(metric);
			}
		}
	}

	/**
	 * 将指定资源实例添加进默认策略
	 */
	@Override
	public boolean addProfileIntoDefult(Long resourceInstanceId,ProfileInfo info) {
		
		try {
			profileService.addMonitorUseDefault(resourceInstanceId);
			profileService.updateProfileBaiscInfo(info);
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
			return false;
		}
		return true;
	}

	/**
	 * 将指定资源实例添加进自定义策略
	 */
	@Override
	public boolean addProfileIntoSpecial(Long resourceInstanceId, Long profileId,ProfileInfo info) {
		
		try {
			profileService.addMonitorUseSpecial(profileId, resourceInstanceId);
			profileService.updateProfileBaiscInfo(info);
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
			return false;
		}
		return true;
	}

	/**
	 * 添加一个个性化策略并加入监控
	 */
	@Override
	public long addPersonalizeProfile(Profile profile,ILoginUser user) {
		long oldProfileId = this.modifyChildProfileResourceRelation(profile,user);
		long newProfileId = -1;
		try {
			newProfileId = profileService.createPersonalizeProfile(profile);
			if(oldProfileId != -1){
				alarmProfileQueryApi.bindAlarmRuleToNewProfile(oldProfileId, newProfileId, AlarmRuleProfileEnum.model_profile);
			}
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
		}
		return newProfileId;
	}

	/**
	 * 添加指定实例到之前个性化策略
	 */
	@Override
	public boolean addInstanceIntoHistoryPersonalProfile(Long profileId,
			Long instanceId,ProfileInfo info) {
		
		try {
			profileService.addMonitorUsePersonalize(profileId, instanceId);
			profileService.updateProfileBaiscInfo(info);
		} catch (ProfilelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
			return false;
		}
		return true;
	}

	/**
	 * 获取接口状态
	 */
	@Override
	public List<MetricData> getInterfaceState(long mainInstanceId,String type) {
		
		List<MetricData> listAllDatas = new ArrayList<MetricData>();
		List<MetricData> listMDatas = new ArrayList<MetricData>();
		ResourceInstance resInstance = new ResourceInstance();
		//可用性指标类型
		String availabilityType = getAvailability(type);
		try {
			//获得主资源实例
			resInstance = resourceInstanceService.getResourceInstance(mainInstanceId);
		} catch (InstancelibException e) {
			e.printStackTrace();
		}
		
		List<Long> childIdList = new ArrayList<Long>();
		List<String> childTypeList = new ArrayList<String>();
		childTypeList.add(ResourceTypeConsts.TYPE_NETINTERFACE);
		childTypeList.add(ResourceTypeConsts.TYPE_SERVICE);
		List<ResourceInstance> childRiList = resInstance.getChildren();
		for (int j = 0; childRiList != null && j < childRiList.size(); j++) {
			ResourceInstance childRi = childRiList.get(j);
			if(!StringUtil.isNull(childRi.getChildType()) && childTypeList.contains(childRi.getChildType())){
				if(childRi.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
					MetricStateData data = metricStateService.getMetricState(childRi.getId(), ResourceOrMetricConst.NETWORK_INTERFACE_AVAILABILITY_METRICID);
					MetricData metricData = new MetricData();
					metricData.setResourceInstanceId(childRi.getId());
					if(data != null){
						if(data.getState().equals(MetricStateEnum.NORMAL)){
							metricData.setData(new String[]{"1"});
						}else{
							metricData.setData(new String[]{"0"});
						}
					}
					listAllDatas.add(metricData);
				}else{
					childIdList.add(childRi.getId());
				}
			}
		}
		
		if(childIdList != null && childIdList.size() > 0){
			int node = Integer.parseInt(resInstance.getDiscoverNode());
			try {
				/**
				 * 获取接口状态
				 */
				List<MetricData> listData = metricDataService.catchRealtimeMetricData(node, childIdList, availabilityType);
				listMDatas.addAll(listData);
				listAllDatas.addAll(listData);
				
			} catch (MetricExecutorException e) {
				logger.error("采集接口状态异常",e);
			}
		}
		
		List<ModuleProp> listModuleProps = new ArrayList<ModuleProp>();
		for (MetricData metricData : listMDatas) {
			ModuleProp moduleProp = new ModuleProp();
			moduleProp.setInstanceId(metricData.getResourceInstanceId());
			moduleProp.setKey(ResourceOrMetricConst.RESOURCE_AVAILABILITY);
			moduleProp.setValues(metricData.getData());
			listModuleProps.add(moduleProp);
		}
		try {
			if(listModuleProps.size() > 0) {
				//更新接口状态信息
				modulePropService.updateProps(listModuleProps);
			}
		} catch (InstancelibException e) {
			logger.error("更新接口可用性指标错误", e);
		}
		return listAllDatas;
	}
	
	/**
	 * 根据资源实例得到该资源实例的可用性指标 标识
	 * @param instance
	 * @return
	 */
	private static String getAvailability(String type) {
		String returnType = "";
		switch (type) {
		case "netWorkInterface":
			returnType = ResourceOrMetricConst.NETWORK_INTERFACE_AVAILABILITY_METRICID;
			break;
		case "hostInterface":
			returnType = ResourceOrMetricConst.HOST_INTERFACE_AVAILABILITY_METRICID;
			break;
		case "service":
			returnType = ResourceOrMetricConst.WMI_HOST_SERVICE_AVAILABILITY_METRICID;
			break;
		default:
			break;
		}
		return returnType;
	}

	/**
	 * 过滤名称获取子资源
	 */
	@Override
	public List<ResourceInstance> getChildResourceInstancesByResourceName(
			Long mainInstanceId, String resourceId, Long domainId,
			ILoginUser user, String interfaceState, String searchContent,
			String resourceName) {
		List<ResourceInstance> resourceChildInstanceList = new ArrayList<ResourceInstance>();

		ResourceInstance resourceInstance = null;
		try {
			resourceInstance = resourceInstanceService.getResourceInstance(mainInstanceId);
		} catch (InstancelibException e1) {
			logger.error(e1.getMessage(),e1);
		}
		
		if(resourceInstance == null){
			return resourceChildInstanceList;
		}
		
		if(resourceInstance.getChildren() == null || resourceInstance.getChildren().size() <= 0){
			return resourceChildInstanceList;
		}
		
		//
		List<ResourceInstance> childrenList = new ArrayList<ResourceInstance>();
		List<ResourceInstance> selectList = new ArrayList<ResourceInstance>();
		//名称过滤
		if (resourceName !=null && !resourceName.equals("")) {
			childrenList = resourceInstance.getChildren();
			for (ResourceInstance resourceChild : childrenList) {
				if(resourceChild.getName().toUpperCase().indexOf(resourceName.toUpperCase()) !=-1){
					selectList.add(resourceChild);
				};
			}
			resourceChildInstanceList.addAll(selectList);
		}else{
			resourceChildInstanceList.addAll(resourceInstance.getChildren());
		}
		
		
		List<ResourceInstance> resultResourceInstanceList = new ArrayList<ResourceInstance>();
		
		for(int j = 0 ; j < resourceChildInstanceList.size() ; j ++){
			
			String curResourceId = resourceChildInstanceList.get(j).getResourceId();
			
			//判断自定义策略的域是否和资源实例相同
			if(domainId != -1 && resourceChildInstanceList.get(j).getDomainId() != domainId.longValue()){
				continue;
			}
			
			if(domainId == -1){
				Set<IDomain> domains = user.getDomains(ILoginUser.RIGHT_RESOURCE);
				boolean isContain = false;
				for(IDomain domain : domains){
					if(resourceChildInstanceList.get(j).getDomainId() == domain.getId()){
						isContain = true;
						break;
					}
				}
				
				if(!isContain){
					continue;
				}
				
			}
			
			if(!curResourceId.equals(resourceId) || resourceChildInstanceList.get(j).getLifeState() == InstanceLifeStateEnum.DELETED
					|| resourceChildInstanceList.get(j).getLifeState() == InstanceLifeStateEnum.INITIALIZE){
				continue;
			}
			
			
			if(interfaceState != null && !(interfaceState.equals(""))){
				//根据接口状态构建资源
				
				//监控的资源查看采集数据，未监控的查看资源模型属性
				if(resourceChildInstanceList.get(j).getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
					MetricStateData data = metricStateService.getMetricState(resourceChildInstanceList.get(j).getId(), ResourceOrMetricConst.NETWORK_INTERFACE_AVAILABILITY_METRICID);
					if("up".equals(interfaceState)) {//"up"只添加接口状态为一的资源
						if (data != null) {
							if (!MetricStateEnum.NORMAL.equals(data.getState())) {
								continue;
							}
						}
					} else if ("down".equals(interfaceState)) {
						if (data != null) {
							if (!MetricStateEnum.CRITICAL.equals(data.getState())) {
								continue;
							}
						}
					}
				}else{
					String[] availableArr = resourceChildInstanceList.get(j).getModulePropBykey(ResourceOrMetricConst.RESOURCE_AVAILABILITY);
					if("up".equals(interfaceState)) {//"up"只添加接口状态为一的资源
						if (availableArr != null && availableArr.length > 0) {
							if (!"1".equals(availableArr[0])) {
								continue;
							}
						}
					} else if ("down".equals(interfaceState)) {
						if (availableArr != null && availableArr.length > 0) {
							if (!"0".equals(availableArr[0])) {
								continue;
							}
						}
					}
				}
			}
			
			//过滤搜索关键字
			if(searchContent != null && !searchContent.equals("")){
				if(resourceChildInstanceList.get(j).getShowName() != null && !resourceChildInstanceList.get(j).getShowName().toLowerCase().contains(searchContent.toLowerCase())){
					//不匹配关键字
					continue;
				}else if(resourceChildInstanceList.get(j).getShowName() == null && resourceChildInstanceList.get(j).getName() != null && !resourceChildInstanceList.get(j).getName().toLowerCase().contains(searchContent.toLowerCase())){
					//不匹配关键字
					continue;
				}
				
			}
				
			resultResourceInstanceList.add(resourceChildInstanceList.get(j));
			
		}
		
		return resultResourceInstanceList;
		
	}
}
