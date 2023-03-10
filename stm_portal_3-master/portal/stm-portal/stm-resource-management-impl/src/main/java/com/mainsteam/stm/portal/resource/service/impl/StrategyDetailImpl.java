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
	 * ????????????ID(???????????????)????????????????????????
	 */
	@Override
	public List<ResourceInstance> getResourceInstancesByIds(String ids,Long domainId,ILoginUser user) {
		String[] resourceIdArray = ids.split(",");
		
		List<ResourceInstance> resourceAllList = new ArrayList<ResourceInstance>();
		
		List<Long> mainResourceInstanceIds = new ArrayList<Long>();
		
		//?????????????????????????????????????????????
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
			
			//??????????????????????????????????????????????????????
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
	 * ????????????ID(???????????????)????????????????????????
	 */
	@Override
	public List<ResourceInstance> getChildResourceInstancesByIds(String ids,Long domainId,ILoginUser user) {
		String[] resourceIdArray = ids.split(",");
		
		List<ResourceInstance> resourceAllList = new ArrayList<ResourceInstance>();
		
		List<Long> mainResourceInstanceIds = new ArrayList<Long>();
		Set<Long> mainResourceInstanceSet = new HashSet<Long>();
		//?????????????????????????????????????????????
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
				//?????????????????????
				continue;
			}
			
			//??????????????????????????????????????????????????????
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
	 * ???????????????????????????????????????????????????
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
			
			//??????????????????????????????????????????????????????
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
				//????????????
				if(instanceBo.getLifeState() == InstanceLifeStateEnum.DELETED 
						|| instanceBo.getLifeState() == InstanceLifeStateEnum.INITIALIZE){
					continue;
				}
			}else if(state == 1){
				//???????????????
				if(instanceBo.getLifeState() == InstanceLifeStateEnum.DELETED
						    || instanceBo.getLifeState() == InstanceLifeStateEnum.INITIALIZE
							|| instanceBo.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED){
					continue;
				}
			}else if(state == 2){
				//???????????????
				if(instanceBo.getLifeState() == InstanceLifeStateEnum.DELETED
					    || instanceBo.getLifeState() == InstanceLifeStateEnum.INITIALIZE
						|| instanceBo.getLifeState() == InstanceLifeStateEnum.MONITORED){
					continue;
				}
			}
			
			//?????????????????????
			if(searchContent != null && !searchContent.equals("")){
				if(instanceBo.getShowName() != null && 
						!instanceBo.getShowName().toLowerCase().contains(searchContent.toLowerCase()) && 
						!instanceBo.getDiscoverIP().toLowerCase().contains(searchContent.toLowerCase())){
					//??????????????????
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
	 * ???????????????????????????????????????????????????
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
			
			//??????????????????????????????????????????????????????
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
				//????????????
				if(instanceBo.getLifeState() == InstanceLifeStateEnum.DELETED 
						|| instanceBo.getLifeState() == InstanceLifeStateEnum.INITIALIZE){
					continue;
				}
			}else if(state == 1){
				//???????????????
				if(instanceBo.getLifeState() == InstanceLifeStateEnum.DELETED
						    || instanceBo.getLifeState() == InstanceLifeStateEnum.INITIALIZE
							|| instanceBo.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED){
					continue;
				}
			}else if(state == 2){
				//???????????????
				if(instanceBo.getLifeState() == InstanceLifeStateEnum.DELETED
					    || instanceBo.getLifeState() == InstanceLifeStateEnum.INITIALIZE
						|| instanceBo.getLifeState() == InstanceLifeStateEnum.MONITORED){
					continue;
				}
			}
			
			//?????????????????????
			if(searchContent != null && !searchContent.equals("")){
				boolean isContainName = false;
				boolean isContainIp = false;
				if(instanceBo.getShowName() != null && instanceBo.getShowName().toLowerCase().contains(searchContent.toLowerCase())){
					//????????????
					isContainName = true;
				}
				if(instanceBo.getDiscoverIP() != null && instanceBo.getDiscoverIP().toLowerCase().contains(searchContent.toLowerCase())){
					//??????IP
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
	 * ???????????????????????????????????????????????????
	 */
	@Override
	public List<ResourceInstance> getExceptChildResourceInstancesByIds(String ids,String resourceId,Long mainProfileId,Long domainId,ILoginUser user) {
		List<ResourceInstance> resourceChildInstanceList = new ArrayList<ResourceInstance>();
		try {
			
			//??????????????????????????????
			Profile profile = profileService.getProfilesById(mainProfileId);
			ProfileInstanceRelation relationsList = profile.getProfileInstanceRelations();
			List<Instance> intanceList = relationsList.getInstances();
			
			//?????????????????????????????????????????????
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
					//??????????????????????????????
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
			
			//??????????????????????????????????????????????????????
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
	 * ???????????????????????????????????????????????????
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
			
			//??????????????????????????????????????????????????????
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
				//??????????????????????????????
				
				//????????????????????????????????????????????????????????????????????????
				if(resourceChildInstanceList.get(j).getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
					MetricStateData data = metricStateService.getMetricState(resourceChildInstanceList.get(j).getId(), ResourceOrMetricConst.NETWORK_INTERFACE_AVAILABILITY_METRICID);
					if("up".equals(interfaceState)) {//"up"????????????????????????????????????
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
					if("up".equals(interfaceState)) {//"up"????????????????????????????????????
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
			
			//?????????????????????
			if(searchContent != null && !searchContent.equals("")){
				if(resourceChildInstanceList.get(j).getShowName() != null && !resourceChildInstanceList.get(j).getShowName().toLowerCase().contains(searchContent.toLowerCase())){
					//??????????????????
					continue;
				}else if(resourceChildInstanceList.get(j).getShowName() == null && resourceChildInstanceList.get(j).getName() != null && !resourceChildInstanceList.get(j).getName().toLowerCase().contains(searchContent.toLowerCase())){
					//??????????????????
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
			
			//??????????????????????????????
			//????????????????????????????????????????????????????????????????????????
			if(resourceInstance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
				MetricStateData data = metricStateService.getMetricState(resourceInstance.getId(), ResourceOrMetricConst.NETWORK_INTERFACE_AVAILABILITY_METRICID);
				if("up".equals(interfaceState)) {//"up"????????????????????????????????????
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
				if("up".equals(interfaceState)) {//"up"????????????????????????????????????
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
			
			//??????????????????ID??????????????????
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
			
			//??????secondTreeAndInstanceMap???????????????????????????ID?????????
			if(!secondTreeAndInstanceMap.containsKey(secondTree.getId())){
				
				secondTreeAndInstanceMap.put(secondTree.getId(), new ArrayList<zTreeBo>());
				
			}
			
			secondTreeAndInstanceMap.get(secondTree.getId()).add(instanceTree);
			
			if(!treeTwoList.contains(secondTree)){
				
				treeTwoList.add(secondTree);
				
			}
			
		}
		
		//???????????????
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
	 * ???????????????
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
	 * ???????????????
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
	 * ???????????????????????????
	 */
	public List<ProfileInfo> getAllDefautProfileInfo(Long resourceId) {
		List<ProfileInfo> infoList = new ArrayList<ProfileInfo>();
		List<ProfileInfo> resultList = new ArrayList<ProfileInfo>();
		try {
			//??????????????????ID??????????????????
			boolean curProfileIsInDefaultOrSpecial = false;
			//??????????????????????????????
			ProfileInfo historyPersonalProfile = profileService.getPersonalizeProfileBasicInfoByResourceInstanceId(resourceId);
			ProfileInfo curProfile = profileService.getBasicInfoByResourceInstanceId(resourceId);
			infoList = profileService.getParentProfileBasicInfoByType(ProfileTypeEnum.DEFAULT);
			infoList.addAll(profileService.getParentProfileBasicInfoByType(ProfileTypeEnum.SPECIAL));
			ResourceInstance instance = resourceInstanceService.getResourceInstance(resourceId);
			String resourceModelId = instance.getResourceId();
			long instanceDomainId = instance.getDomainId();
			for(ProfileInfo info : infoList){
				if(info.getProfileId() == curProfile.getProfileId()){
					//????????????????????????????????????
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
	 * ???????????????ID?????????????????????
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
	 * ???????????????
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
	 * ????????????????????????
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
	 * ?????????????????????
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
	 * ?????????????????????
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
	 * ??????????????????????????????
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
	 * ??????????????????????????????
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
	 * ????????????????????????
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
	 * ????????????????????????
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
	 * ????????????????????????????????????
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
	 * ??????????????????ID??????????????????
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
	 * ??????????????????????????????????????????????????????????????????????????????
	 */
	@Override
	public long modifyChildProfileResourceRelation(Profile profile,ILoginUser user) {
		//???????????????
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
		
		//??????????????????
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
//			//??????????????????????????????
//			modifyMetricSetting(oldProfile.getChildren().get(i),profile.getChildren().get(i));
//		}
		
		return oldProfileId;
	}
	
	//????????????????????????????????????
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
				//????????????
				newProfile.getMetricSetting().getMetrics().add(metric);
			}
		}
	}

	/**
	 * ??????????????????????????????????????????
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
	 * ?????????????????????????????????????????????
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
	 * ??????????????????????????????????????????
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
	 * ??????????????????????????????????????????
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
	 * ??????????????????
	 */
	@Override
	public List<MetricData> getInterfaceState(long mainInstanceId,String type) {
		
		List<MetricData> listAllDatas = new ArrayList<MetricData>();
		List<MetricData> listMDatas = new ArrayList<MetricData>();
		ResourceInstance resInstance = new ResourceInstance();
		//?????????????????????
		String availabilityType = getAvailability(type);
		try {
			//?????????????????????
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
				 * ??????????????????
				 */
				List<MetricData> listData = metricDataService.catchRealtimeMetricData(node, childIdList, availabilityType);
				listMDatas.addAll(listData);
				listAllDatas.addAll(listData);
				
			} catch (MetricExecutorException e) {
				logger.error("????????????????????????",e);
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
				//????????????????????????
				modulePropService.updateProps(listModuleProps);
			}
		} catch (InstancelibException e) {
			logger.error("?????????????????????????????????", e);
		}
		return listAllDatas;
	}
	
	/**
	 * ????????????????????????????????????????????????????????? ??????
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
	 * ???????????????????????????
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
		//????????????
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
			
			//??????????????????????????????????????????????????????
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
				//??????????????????????????????
				
				//????????????????????????????????????????????????????????????????????????
				if(resourceChildInstanceList.get(j).getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
					MetricStateData data = metricStateService.getMetricState(resourceChildInstanceList.get(j).getId(), ResourceOrMetricConst.NETWORK_INTERFACE_AVAILABILITY_METRICID);
					if("up".equals(interfaceState)) {//"up"????????????????????????????????????
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
					if("up".equals(interfaceState)) {//"up"????????????????????????????????????
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
			
			//?????????????????????
			if(searchContent != null && !searchContent.equals("")){
				if(resourceChildInstanceList.get(j).getShowName() != null && !resourceChildInstanceList.get(j).getShowName().toLowerCase().contains(searchContent.toLowerCase())){
					//??????????????????
					continue;
				}else if(resourceChildInstanceList.get(j).getShowName() == null && resourceChildInstanceList.get(j).getName() != null && !resourceChildInstanceList.get(j).getName().toLowerCase().contains(searchContent.toLowerCase())){
					//??????????????????
					continue;
				}
				
			}
				
			resultResourceInstanceList.add(resourceChildInstanceList.get(j));
			
		}
		
		return resultResourceInstanceList;
		
	}
}
