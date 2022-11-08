package com.mainsteam.stm.portal.resource.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.license.calc.api.ILicenseCapacityCategory;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricInfo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.SnapshotProfileApi;
import com.mainsteam.stm.portal.resource.bo.ResourceCategoryTreeBo;
import com.mainsteam.stm.portal.resource.bo.SnapshotProfilePageBo;
import com.mainsteam.stm.portal.resource.bo.SnapshotResourceInstance;
import com.mainsteam.stm.portal.resource.bo.SnapshotResourceMetric;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.fault.ProfileFaultInstanceService;
import com.mainsteam.stm.profilelib.fault.ProfileFaultService;
import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultInstance;
import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultMetric;
import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultRelation;
import com.mainsteam.stm.profilelib.fault.obj.Profilefault;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.resource.bo.ResourceQueryBo;
import com.mainsteam.stm.system.scriptmanage.api.IScriptManageApi;
import com.mainsteam.stm.system.scriptmanage.bo.ScriptManage;
import com.mainsteam.stm.system.scriptmanage.bo.ScriptManageTypeEnum;

public class SnapshotProfileImpl implements SnapshotProfileApi{
	private static final Log logger = LogFactory.getLog(SnapshotProfileImpl.class);
	@Resource
	private CapacityService capacityService;
	@Resource
	private IResourceApi stm_system_resourceApi;
	@Resource
	private ResourceInstanceService resourceInstanceService;
	@Resource
	private ProfileService profileService;
	@Autowired
	@Qualifier("scriptManageService")
	private IScriptManageApi scriptManageService;
	@Resource
	private ProfileFaultService profileFaultService;
	@Resource
	private ProfileFaultInstanceService profilefaultInstanceService;
	
	@Resource
	private ILicenseCapacityCategory licenseCapacityCategory;
	
	@Resource
	private CustomMetricService customMetricService;
	
	@Override
	public List<ResourceCategoryTreeBo> getAllResourceCategory() {
		List<ResourceCategoryTreeBo> category = new ArrayList<ResourceCategoryTreeBo>();
		this.loadResourceCategory(category,capacityService.getRootCategory());
		return category;
	}
	private void loadResourceCategory(List<ResourceCategoryTreeBo> resourceCategory,CategoryDef def){
		if(!def.isDisplay()){
			return;
		}
		 if(!licenseCapacityCategory.isAllowCategory(def.getId())){
			return;
		 }
		ResourceCategoryTreeBo category = new ResourceCategoryTreeBo();
		category.setId(def.getId());
		category.setName(def.getName());
		if (null != def.getParentCategory()) {
			category.setPid(def.getParentCategory().getId());
			category.setState("closed");
			category.setType(1);
			resourceCategory.add(category);
		}
		// 判断是否还有child category
		if (null != def.getChildCategorys()) {
			CategoryDef[] categoryDefs = def.getChildCategorys();
			for (int i = 0; i < categoryDefs.length; i++) {
				if(categoryDefs[i].isDisplay())
					loadResourceCategory(resourceCategory,categoryDefs[i]);
			}
		}else{
			if(null != def.getResourceDefs()){
				ResourceDef[] resourceDefs = def.getResourceDefs();
				for (int i = 0; i < resourceDefs.length; i++) {
					ResourceDef resourceDef = resourceDefs[i];
					Set<String> reqPluginIds = resourceDef.getRequiredCollPluginIds();
					if(!isNetwork_Device(def) && reqPluginIds != null && reqPluginIds.contains("SnmpPlugin")){
						continue;
					}
					ResourceCategoryTreeBo resource = new ResourceCategoryTreeBo();
					resource.setId(resourceDef.getId());
					resource.setName(resourceDef.getName());
					resource.setType(2);
					resource.setPid(def.getId());
					resourceCategory.add(resource);
				}
			}
		}
	}
	
	private boolean isNetwork_Device(CategoryDef def){
		if(CapacityConst.NETWORK_DEVICE.equals(def.getId())){
			return true;
		}else{
			if(def.getParentCategory() != null){
				return isNetwork_Device(def.getParentCategory());
			}
		}
		return false;
	}
	
	@Override
	public List<ResourceCategoryTreeBo> getChildResourceByResourceCategory(
			String categoryId) {
		Map<String, ResourceCategoryTreeBo> childResourceMap = new HashMap<String, ResourceCategoryTreeBo>();
		
		CategoryDef def = capacityService.getCategoryById(categoryId);
		
		this.loadChildResource(def,childResourceMap);
		
		List<ResourceCategoryTreeBo> childResourceList = new ArrayList<ResourceCategoryTreeBo>(childResourceMap.values());
		return childResourceList;
	}
	private void loadChildResource(CategoryDef def,Map<String, ResourceCategoryTreeBo> childResourceMap){
		if(null != def.getChildCategorys()){
			for(CategoryDef childDef : def.getChildCategorys()){
				loadChildResource(childDef,childResourceMap);
			}
		}else{
			ResourceDef[] mainDefs = def.getResourceDefs();
			if(mainDefs == null){
				if(logger.isErrorEnabled()){
					logger.error("Get main resource error,categoryId = " + def.getId());
				}
				return;
			}
			
			for(ResourceDef parentResource : mainDefs){
				
				ResourceDef[] childDefs = parentResource.getChildResourceDefs();
				if(childDefs == null){
					if(logger.isErrorEnabled()){
						logger.error("ParentResource.getChildResourceDefs() is null,resourceId = " + parentResource.getId());
					}
					continue;
				}
				for(ResourceDef childResource : childDefs){
					if(!childResourceMap.containsKey(childResource.getName())){
						ResourceCategoryTreeBo resultDef = new ResourceCategoryTreeBo();
						resultDef.setId(childResource.getId());
						resultDef.setName(childResource.getName());
						childResourceMap.put(childResource.getName(), resultDef);
					}else{
						String id = childResourceMap.get(childResource.getName()).getId();
						id += "," + childResource.getId();
						childResourceMap.get(childResource.getName()).setId(id);
					}
				}
				
			}
		}
	}

	@Override
	public List<ResourceCategoryTreeBo> getChildResourceByMainResource(
			String resourceId) {
		List<ResourceCategoryTreeBo> childResourceList = new ArrayList<ResourceCategoryTreeBo>();
		ResourceDef mainDef = capacityService.getResourceDefById(resourceId);
		if (mainDef == null) {
			if (logger.isErrorEnabled()) {
				logger.error("Get main resource error,resourceId = " + resourceId);
			}
			return childResourceList;
		}
		ResourceDef[] childDefs = mainDef.getChildResourceDefs();

		if (childDefs == null || childDefs.length <= 0) {
			if (logger.isErrorEnabled()) {
				logger.error("Get child resource error,MainResourceId = " + resourceId);
			}
			return childResourceList;
		}
		for (ResourceDef childResource : childDefs) {
			ResourceCategoryTreeBo resultDef = new ResourceCategoryTreeBo();
			resultDef.setId(childResource.getId());
			resultDef.setName(childResource.getName());
			childResourceList.add(resultDef);
		}
		return childResourceList;
	}
	@Override
	public List<SnapshotResourceInstance> getResourceInstanceByCategoryId(String categoryId, Long domainId, ILoginUser user) {
		List<SnapshotResourceInstance> instanceList = new ArrayList<SnapshotResourceInstance>();
		CategoryDef def = capacityService.getCategoryById(categoryId);
		if(def == null){
			if(logger.isErrorEnabled()){
				logger.error("Get categoryDef is null,categoryId = " + categoryId);
			}
			return instanceList;
		}
		
		//获取叶子类别
		List<CategoryDef> leafCategoryList = capacityService.getLeafCategoryList(def);
		List<String> leafCategoryNameList = new ArrayList<String>();
		
		if(leafCategoryList == null){
			if(logger.isErrorEnabled()){
				logger.error("Get leafCategryList is null,categoryId = " + categoryId);
			}
			return instanceList;
		}
		
		for(CategoryDef leafDef : leafCategoryList){
			leafCategoryNameList.add(leafDef.getId());
		}
		
		ResourceQueryBo queryBo = new ResourceQueryBo(user);
		queryBo.setCategoryIds(leafCategoryNameList);
		List<Long> domainIds = new ArrayList<Long>();
		domainIds.add(domainId);
		queryBo.setDomainIds(domainIds);
		List<ResourceInstanceBo> instances = stm_system_resourceApi.getResources(queryBo);
		if(instances != null && instances.size() > 0){
			for(ResourceInstanceBo instance : instances){
				//判断实例是否监控和是否属于当前用户域
				if(instance.getLifeState() != InstanceLifeStateEnum.MONITORED){
					continue;
				}
				SnapshotResourceInstance reportInstance = new SnapshotResourceInstance();
				BeanUtils.copyProperties(instance, reportInstance);
				if(reportInstance.getShowName() == null || reportInstance.getShowName().equals("")){
					reportInstance.setShowName(instance.getName());
				}
				ResourceDef resourceDef = capacityService.getResourceDefById(reportInstance.getResourceId());
				if(resourceDef == null){
					if(logger.isErrorEnabled()){
						logger.error("Get resourceDef is null ,resourceId : " + reportInstance.getResourceId());
					}
				}else{
					reportInstance.setResourceName(resourceDef.getName());
					instanceList.add(reportInstance);
				}
			}
		}
		return instanceList;
	}
	@Override
	public List<SnapshotResourceInstance> getInstanceByResource(String resourceId, Long domainId, ILoginUser user) {
		List<SnapshotResourceInstance> instances = new ArrayList<SnapshotResourceInstance>();
		
		if(resourceId.contains(",")){
			//有多个资源
			for(String singleId : resourceId.split(",")){
				try {
					List<ResourceInstance> instanceList = resourceInstanceService.getResourceInstanceByResourceId(singleId);
					if(instanceList == null || instanceList.size() <= 0){
						continue;
					}
					List<Long> mainResourceIntanceIds = new ArrayList<Long>();
					Set<Long> mainResourceIntanceIdsSet = new HashSet<Long>();
					for(ResourceInstance instance : instanceList){
						//找出所有主资源实例
						mainResourceIntanceIdsSet.add(instance.getParentId());
					}
					mainResourceIntanceIds = new ArrayList<Long>(mainResourceIntanceIdsSet);
					ResourceQueryBo queryBo = new ResourceQueryBo(user);
					List<Long> domainIds = new ArrayList<Long>();
					domainIds.add(domainId);
					queryBo.setDomainIds(domainIds);
					List<Long> accessMainResourceIntancesIds = stm_system_resourceApi.accessFilter(queryBo, mainResourceIntanceIds);
					
					ResourceDef def = capacityService.getResourceDefById(singleId);
					if(def == null){
						if(logger.isErrorEnabled()){
							logger.error("Get resourceDef is null by capacityService.getResourceDefById , id = " + singleId);
						}
						continue;
					}
					
					for(ResourceInstance instance : instanceList){
						//判断该子资源是否属于用户
						if(!accessMainResourceIntancesIds.contains(instance.getParentId())){
							continue;
						}
						//判断实例是否监控和是否属于当前用户域
						if(instance.getLifeState() != InstanceLifeStateEnum.MONITORED){
							continue;
						}
						SnapshotResourceInstance reportInstance = new SnapshotResourceInstance();
						reportInstance.setId(instance.getId());
						reportInstance.setResourceId(singleId);
						reportInstance.setShowName(instance.getShowName());
						if(reportInstance.getShowName() == null || reportInstance.getShowName().equals("")){
							reportInstance.setShowName(instance.getName());
						}

						reportInstance.setResourceName(def.getName());
						reportInstance.setDiscoverIP(resourceInstanceService.getResourceInstance(instance.getParentId()).getShowIP());
						instances.add(reportInstance);
					}
				} catch (InstancelibException e) {
					if(logger.isErrorEnabled()){
						logger.error(e.getMessage(),e);
					}
				}
			}
		}else{
			//一个资源
			try {
				ResourceDef resourceDef = capacityService.getResourceDefById(resourceId);
				if(resourceDef.getParentResourceDef() != null){
					//子资源
					List<ResourceInstance> instanceList = resourceInstanceService.getResourceInstanceByResourceId(resourceId);
					if(instanceList == null || instanceList.size() <= 0){
						return instances;
					}
					List<Long> mainResourceIntanceIds = new ArrayList<Long>();
					Set<Long> mainResourceIntanceIdsSet = new HashSet<Long>();
					for(ResourceInstance instance : instanceList){
						//找出所有主资源实例
						mainResourceIntanceIdsSet.add(instance.getParentId());
					}
					mainResourceIntanceIds = new ArrayList<Long>(mainResourceIntanceIdsSet);
					ResourceQueryBo queryBo = new ResourceQueryBo(user);
					List<Long> domainIds = new ArrayList<Long>();
					domainIds.add(domainId);
					queryBo.setDomainIds(domainIds);
					List<Long> accessMainResourceIntancesIds = stm_system_resourceApi.accessFilter(queryBo, mainResourceIntanceIds);
					if(instanceList != null && instanceList.size() >0){
						for(ResourceInstance instance : instanceList){
							//判断该子资源是否属于用户
							if(!accessMainResourceIntancesIds.contains(instance.getParentId())){
								continue;
							}
							//判断实例是否监控和是否属于当前用户域
							if(instance.getLifeState() != InstanceLifeStateEnum.MONITORED || domainId != instance.getDomainId()){
								continue;
							}
							SnapshotResourceInstance reportInstance = new SnapshotResourceInstance();
							BeanUtils.copyProperties(instance, reportInstance);
							if(reportInstance.getShowName() == null || reportInstance.getShowName().equals("")){
								reportInstance.setShowName(instance.getName());
							}
							ResourceDef def = capacityService.getResourceDefById(reportInstance.getResourceId());
							if(def == null){
								if(logger.isErrorEnabled()){
									logger.error("Get resourceDef is null by capacityService.getResourceDefById , id = " + reportInstance.getResourceId());
								}
								continue;
							}
							reportInstance.setResourceName(def.getName());
							if(instance.getParentId() > 0){
								reportInstance.setDiscoverIP(resourceInstanceService.getResourceInstance(instance.getParentId()).getShowIP());
							}
							instances.add(reportInstance);
						}
					}
				}else{
					//主资源
					ResourceQueryBo queryBo = new ResourceQueryBo(user);
					List<Long> domainIds = new ArrayList<Long>();
					domainIds.add(domainId);
					queryBo.setModuleId(resourceId);
					queryBo.setDomainIds(domainIds);
					List<ResourceInstanceBo> instanceList = stm_system_resourceApi.getResources(queryBo);
					if(instanceList != null && instanceList.size() >0){
						for(ResourceInstanceBo instance : instanceList){
							//判断实例是否监控和是否属于当前用户域
							if(instance.getLifeState() != InstanceLifeStateEnum.MONITORED){
								continue;
							}
							SnapshotResourceInstance reportInstance = new SnapshotResourceInstance();
							BeanUtils.copyProperties(instance, reportInstance);
							if(reportInstance.getShowName() == null || reportInstance.getShowName().equals("")){
								reportInstance.setShowName(instance.getName());
							}
							ResourceDef def = capacityService.getResourceDefById(reportInstance.getResourceId());
							if(def == null){
								if(logger.isErrorEnabled()){
									logger.error("Get resourceDef is null by capacityService.getResourceDefById , id = " + reportInstance.getResourceId());
								}
								continue;
							}
							reportInstance.setResourceName(def.getName());
							instances.add(reportInstance);
						}
					}
				}
			} catch (InstancelibException e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage(),e);
				}
			}
		}
		return instances;
	}
	@Override
	public List<SnapshotResourceMetric> getMetricListByResource(Set<String> resourceIdList, Long[] instanceIdArr) {
		Map<String, List<SnapshotResourceMetric>> metricMap = new HashMap<String, List<SnapshotResourceMetric>>();
		
		for(String resourceId : resourceIdList){
			ResourceDef def = capacityService.getResourceDefById(resourceId);
			if(def == null){
				if(logger.isErrorEnabled()){
					logger.error("CapacityService.getResourceDefById is null,resourceId : " + resourceId);
				}
				continue;
			}
			Map<Long, SnapshotResourceMetric> performanceMetricMap = new TreeMap<Long, SnapshotResourceMetric>();
			for(ResourceMetricDef metricDef : def.getMetricDefs()){
				if(metricDef.getMetricType() == MetricTypeEnum.PerformanceMetric
						|| metricDef.getMetricType() == MetricTypeEnum.InformationMetric){
					SnapshotResourceMetric metric = new SnapshotResourceMetric();
					BeanUtils.copyProperties(metricDef, metric);
					metric.setMetricSort(-1);
					if(metricDef.getUnit() != null && !metricDef.getUnit().trim().equals("")){
						metric.setName(metric.getName() + "(" + metricDef.getUnit() + ")");
						metric.setUnit(metricDef.getUnit());
					}
					performanceMetricMap.put(Long.parseLong(metricDef.getDisplayOrder()), metric);
				}
			}
			metricMap.put(resourceId, new ArrayList<SnapshotResourceMetric>(performanceMetricMap.values()));
		}
		
		//取所有模型指标集合的交集
		List<SnapshotResourceMetric> metricList = new ArrayList<SnapshotResourceMetric>();
		for(String resourceId : metricMap.keySet()){
			if(metricList.size() <= 0){
				metricList = metricMap.get(resourceId);
				continue;
			}
			List<SnapshotResourceMetric> metricLists = metricMap.get(resourceId);
			metricList.retainAll(metricLists);
		}
		
		//自定义指标集合交集
		Map<String,SnapshotResourceMetric> customMetricMap = new HashMap<String,SnapshotResourceMetric>();
		List<String> customMetricList = null;
			for(Long instanceId:instanceIdArr){
				try {
					List<CustomMetric> cmList = customMetricService.getCustomMetricsByInstanceId(instanceId);
					List<String> customMetricTemp = new ArrayList<String>();
					if(null!=cmList && cmList.size()>0){
						for(CustomMetric cm:cmList){
							CustomMetricInfo cmi = cm.getCustomMetricInfo();
							if(null!=cmi && cmi.getStyle()==MetricTypeEnum.PerformanceMetric && cmi.isMonitor()){
								if(!customMetricMap.containsKey(cmi.getId())){
									SnapshotResourceMetric srm = customToSRmetric(cmi);
									customMetricMap.put(cmi.getId(), srm);
								}
							}
							customMetricTemp.add(cmi.getId());
						}
					}
					//记录哪些指标交集
					if(null==customMetricList){
						customMetricList = customMetricTemp;
					}else{
						customMetricList.retainAll(customMetricTemp);
					}
				} catch (CustomMetricException e) {
					if(logger.isErrorEnabled()){
						logger.error("Get resource customMetricList error , id = " + instanceId);
						logger.error(e.getMessage());
					}
				}
			}
			if(null!=customMetricList){
				for(String id:customMetricList){
					SnapshotResourceMetric srm = customMetricMap.get(id);
					if(null!=srm){
						metricList.add(srm);
					}
				}
			}
			
		return metricList;
	}
	
	private static SnapshotResourceMetric customToSRmetric(CustomMetricInfo cmi){
		SnapshotResourceMetric sr = new SnapshotResourceMetric();
		sr.setId(cmi.getId());
		sr.setName(cmi.getName());
		sr.setUnit(cmi.getUnit());
		sr.setMetricSort(-1);
		return sr;
	}
	
	@Override
	public List<ScriptManage> getSnapshotScript() {
		return scriptManageService.loadAllByTypeCode(ScriptManageTypeEnum.SNAPSHOT);
	}
	@Override
	public List<ScriptManage> getResumScript() {
		return scriptManageService.loadAllByTypeCode(ScriptManageTypeEnum.RECOVER);
	}
	@Override
	public boolean addSnapshotProfile(Profilefault pff, String instanceIds, String metricIds, ILoginUser user) {
		ProfileFaultRelation pfr = new ProfileFaultRelation();
		// 处理告警级别
		if(pff.getAlarmLevel() != null && !"".equals(pff.getAlarmLevel())){
			String alarmLevelString = pff.getAlarmLevel();
			String[] alarmLevel = alarmLevelString.split(",");
			for(int i = 0; alarmLevel != null && i < alarmLevel.length; i++){
				switch (alarmLevel[i]) {
				case "CRITICAL":
					alarmLevelString += ",CRITICAL_NOTHING";
					break;
//				case "UNKOWN":
//					alarmLevelString += ",UNKNOWN_NOTHING";
//					break;
				}
			}
			pff.setAlarmLevel(alarmLevelString);
		}
		pfr.setAlarmLevel(pff.getAlarmLevel());
		pfr.setCreateUser(user.getName());
		pfr.setIsUse(pff.getIsUse());
		pfr.setParentResourceId(pff.getParentResourceId());
		pfr.setProfileDesc(pff.getProfileDesc());
		pfr.setProfileName(pff.getProfileName());
		pfr.setRecoveryScriptId(pff.getRecoveryScriptId());
		pfr.setResourceId(pff.getResourceId());
		pfr.setSnapshotScriptId(pff.getSnapshotScriptId());
		pfr.setDomainId(pff.getDomainId());
		List<ProfileFaultInstance> pfiList = new ArrayList<ProfileFaultInstance>();
		if(instanceIds != null && !"".equals(instanceIds.trim())){
			for(int i = 0; i < instanceIds.split(",").length; i++){
				ProfileFaultInstance pfi = new ProfileFaultInstance();
				pfi.setInstanceId(instanceIds.split(",")[i]);
				pfiList.add(pfi);
			}
		}
		pfr.setProfileFaultInstances(pfiList);
		
		List<ProfileFaultMetric> pfmList = new ArrayList<ProfileFaultMetric>();
		if(metricIds != null && !"".equals(metricIds.trim())){
			for(int i = 0; i < metricIds.split(",").length; i++){
				ProfileFaultMetric pfm = new ProfileFaultMetric();
				pfm.setMetricId(metricIds.split(",")[i]);
				pfmList.add(pfm);
			}
		}
		pfr.setProfileFaultMetrics(pfmList);
		
		ProfileFaultRelation newPfr = profileFaultService.insertProfileFault(pfr);
		
		return newPfr != null && newPfr.getProfileId() != 0 ? true : false;
	}
	@Override
	public boolean editSnapshotProfile(Profilefault pff, String instanceIds,
			String metricIds, ILoginUser user) {
		ProfileFaultRelation pfr = new ProfileFaultRelation();
		pfr.setAlarmLevel(pff.getAlarmLevel());
		pfr.setCreateUser(user.getName());
		pfr.setIsUse(pff.getIsUse());
		pfr.setParentResourceId(pff.getParentResourceId());
		pfr.setProfileDesc(pff.getProfileDesc());
		pfr.setProfileId(pff.getProfileId());
		pfr.setProfileName(pff.getProfileName());
		pfr.setRecoveryScriptId(pff.getRecoveryScriptId());
		pfr.setResourceId(pff.getResourceId());
		pfr.setSnapshotScriptId(pff.getSnapshotScriptId());
		pfr.setUpdateTime(new Date());
		pfr.setUpdateUser(user.getName());
		pfr.setDomainId(pff.getDomainId());
		List<ProfileFaultInstance> pfiList = new ArrayList<ProfileFaultInstance>();
		if(instanceIds != null && !"".equals(instanceIds.trim())){
			for(int i = 0; i < instanceIds.split(",").length; i++){
				ProfileFaultInstance pfi = new ProfileFaultInstance();
				pfi.setInstanceId(instanceIds.split(",")[i]);
				pfiList.add(pfi);
			}
		}
		pfr.setProfileFaultInstances(pfiList);
		
		List<ProfileFaultMetric> pfmList = new ArrayList<ProfileFaultMetric>();
		if(metricIds != null && !"".equals(metricIds.trim())){
			for(int i = 0; i < metricIds.split(",").length; i++){
				ProfileFaultMetric pfm = new ProfileFaultMetric();
				pfm.setMetricId(metricIds.split(",")[i]);
				pfmList.add(pfm);
			}
		}
		pfr.setProfileFaultMetrics(pfmList);
		
		return profileFaultService.updateProfilefault(pfr) != 0 ? true : false;
	}
	@Override
	public SnapshotProfilePageBo getAllSnapshotProfile(SnapshotProfilePageBo spPBo,ILoginUser user) {
		long rowCount = spPBo.getRowCount();
		long startRow = spPBo.getStartRow();
		Page<Profilefault, Profilefault> page = new Page<Profilefault, Profilefault>();
		page.setOrder(spPBo.getOrder());
		page.setRowCount(10000L);
		page.setSort(spPBo.getSort());
		page.setStartRow(0);
		profileFaultService.queryProfilefaults(page);
		
		List<Profilefault> list = page.getDatas();
		
		if(list == null || list.size() <= 0){
			spPBo.setTotalRecord(0);
			spPBo.setPffList(new ArrayList<Profilefault>());
			return spPBo;
		}
		
		List<Profilefault> resultList = new ArrayList<Profilefault>();
		
		if(user.isSystemUser()){
			resultList = list;
		}else{
			Set<IDomain> domains = user.getDomains(ILoginUser.RIGHT_RESOURCE);
			
			for(Profilefault fault : list){
				long profileDomain = Long.parseLong(fault.getDomainId());
				boolean isContainDomain = false;
				for(IDomain domain : domains){
					if(profileDomain == domain.getId()){
						isContainDomain = true;
						break;
					}
				}
				
				if(isContainDomain){
					resultList.add(fault);
				}
			}
		}
		
		spPBo.setTotalRecord(resultList.size());
		
		//分页操作
		List<Profilefault> resultPageList = new ArrayList<Profilefault>();
		if(resultList.size() < (int)(rowCount + startRow)){
			resultPageList = resultList.subList((int)startRow,resultList.size());
		}else{
			resultPageList = resultList.subList((int)startRow, (int)(rowCount + startRow));
		}
		spPBo.setPffList(resultPageList);
		
		return spPBo;
		
	}
	@Override
	public boolean delSnapshotProfile(String profileIds) {
		boolean flag = false;
		if(profileIds != null && !"".equals(profileIds.trim())){
			Long[] profileId = new Long[profileIds.split(",").length];
			for(int i = 0; i < profileIds.split(",").length; i++){
				profileId[i] = Long.valueOf(profileIds.split(",")[i]);
			}
			flag = profileFaultService.removeProfileFault(profileId) > 0;
		}
		return flag;
	}
	@Override
	public boolean updateIsUse(String profileId) {
		return profileFaultService.updateProfilefaultState(Long.valueOf(profileId)) > 0;
	}
	@Override
	public Map<String, List<String>> getSnapshotProfileRelation(String profileId) {
		Map<String, List<String>> relation = new HashMap<String, List<String>>();
		ProfileFaultRelation pfr = profileFaultService.getProfileFaultRelationById(Long.valueOf(profileId));
		
		List<String> pfiRelation = new ArrayList<String>();
		List<ProfileFaultInstance> pfiList = pfr.getProfileFaultInstances();
		for(int i = 0; pfiList != null && i < pfiList.size(); i++){
			pfiRelation.add(pfiList.get(i).getInstanceId());
		}
		relation.put("instance", pfiRelation);
		List<String> pfmRelation = new ArrayList<String>();
		List<ProfileFaultMetric> pfmList = pfr.getProfileFaultMetrics();
		for(int i = 0; pfmList != null && i < pfmList.size(); i++){
			pfmRelation.add(pfmList.get(i).getMetricId());
		}
		relation.put("metric", pfmRelation);
		return relation;
	}
}
