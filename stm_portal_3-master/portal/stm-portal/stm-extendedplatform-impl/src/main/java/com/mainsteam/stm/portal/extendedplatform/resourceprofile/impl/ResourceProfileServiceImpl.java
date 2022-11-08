package com.mainsteam.stm.portal.extendedplatform.resourceprofile.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.PerfMetricStateEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.caplib.resource.ThresholdDef;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.instancelib.dao.PropDAO;
import com.mainsteam.stm.instancelib.dao.PropTypeDAO;
import com.mainsteam.stm.instancelib.dao.RelationDAO;
import com.mainsteam.stm.instancelib.dao.ResourceInstanceDAO;
import com.mainsteam.stm.instancelib.util.ResourceInstanceCache;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.extendedplatform.resourceprofile.api.ResourceProfileService;
import com.mainsteam.stm.portal.extendedplatform.resourceprofile.dao.ProfileDao;
import com.mainsteam.stm.portal.extendedplatform.resourceprofile.po.CategoryPo;
import com.mainsteam.stm.portal.extendedplatform.resourceprofile.po.ProfileInstancePo;
import com.mainsteam.stm.profilelib.TimelineService;
import com.mainsteam.stm.profilelib.dao.ProfileChangeDAO;
import com.mainsteam.stm.profilelib.dao.ProfileDAO;
import com.mainsteam.stm.profilelib.dao.ProfileInstanceRelationDAO;
import com.mainsteam.stm.profilelib.dao.ProfileMetricDAO;
import com.mainsteam.stm.profilelib.dao.ProfileThresholdDAO;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.profilelib.obj.TimelineInfo;
import com.mainsteam.stm.profilelib.objenum.ProfileChangeEnum;
import com.mainsteam.stm.profilelib.po.ProfileChangePO;
import com.mainsteam.stm.profilelib.po.ProfileInfoPO;
import com.mainsteam.stm.profilelib.po.ProfileInstRelationPO;
import com.mainsteam.stm.profilelib.po.ProfileMetricPO;
import com.mainsteam.stm.profilelib.po.ProfileThresholdPO;
import com.mainsteam.stm.profilelib.util.ProfileCache;

@Service("resourceProfileService")
public class ResourceProfileServiceImpl implements ResourceProfileService {

	Logger logger = LoggerFactory.getLogger(ResourceProfileServiceImpl.class);
	@Autowired
	private CapacityService capacityService;
	
	@Resource(name="extendProfileDao")
	private ProfileDao extendProfileDao;
	
	@Resource
	private ProfileDAO profileDAO;
	@Resource
	private ResourceInstanceDAO resourceInstanceDAO;
	@Resource
	private PropDAO instancePropDAO;
	@Resource
	private PropTypeDAO instancePropTypeDAO;
	@Resource
	private RelationDAO instanceRelationDAO;
	@Resource
	private ProfileInstanceRelationDAO profileInstRelDAO;
	@Resource
	private ProfileMetricDAO profileMetricDAO;
	@Resource
	private ProfileThresholdDAO profileThresholdDAO;
	@Resource
	private ProfileChangeDAO profileChangeDAO;
	@Resource
	private TimelineService timelineService;
	@Resource
	private ISequence ocProfileChangeSequence;
	
	@Resource
	private ISequence ocProfilelibMetricSequence;
	@Resource
	private ISequence ocProfilelibThresholdSequence;
	@Resource
	private ProfileCache profileCache;
	@Resource
	private ResourceInstanceCache instanceCache;

	@Override
	public List<CategoryPo> queryAllResourceDefs() {
		List<CategoryDef> categoryDefs = capacityService.getCategoryList(1);
		List<CategoryPo> categoryPos = convertCategoryDefs(categoryDefs);
		return categoryPos;
	}
	
	public List<CategoryPo> queryAllParentResource(){
		List<CategoryDef> categoryDefs = capacityService.getCategoryList(1);
		List<CategoryPo> categoryPos = new ArrayList<CategoryPo>();
		convertCategoryToList(categoryDefs,categoryPos);
		return categoryPos;
	}
	
	private void convertCategoryToList(List<CategoryDef> categoryDefs,List<CategoryPo> categoryPos){
		if(categoryPos==null){
			categoryPos = new ArrayList<CategoryPo>();
		}
		if(categoryDefs!=null && !categoryDefs.isEmpty()){
			for (CategoryDef categoryDef : categoryDefs) {
				CategoryPo categoryPo = new CategoryPo();
				categoryPo.setId(categoryDef.getId());
				categoryPo.setName(categoryDef.getName());
				if(categoryDef.getChildCategorys()!=null && categoryDef.getChildCategorys().length>0){
					this.convertCategoryToList(Arrays.asList(categoryDef.getChildCategorys()),categoryPos);
				}else{
					if(categoryDef.getResourceDefs()!=null && categoryDef.getResourceDefs().length>0){
						categoryPos.addAll(this.convertResourceToCategory(categoryDef.getResourceDefs()));
					}else{
						categoryPos.add(categoryPo);
					}
				}
			}
		}
	}
	
	
	private List<CategoryPo> convertCategoryDefs(List<CategoryDef> categoryDefs){
		List<CategoryPo> categoryPos = new ArrayList<CategoryPo>();
		if(categoryDefs!=null && !categoryDefs.isEmpty()){
			for (CategoryDef categoryDef : categoryDefs) {
				CategoryPo categoryPo = new CategoryPo();
				categoryPo.setId(categoryDef.getId());
				categoryPo.setName(categoryDef.getName());
				if(categoryDef.getChildCategorys()!=null && categoryDef.getChildCategorys().length>0){
					categoryPo.setChildCategorys(this.convertCategoryDefs(Arrays.asList(categoryDef.getChildCategorys())));
				}
				if(categoryDef.getResourceDefs()!=null && categoryDef.getResourceDefs().length>0){
					categoryPo.setChildCategorys(this.convertResourceToCategory(categoryDef.getResourceDefs()));
				}
				categoryPos.add(categoryPo);
			}
		}
		return categoryPos;
		
	}

	
	private List<CategoryPo> convertResourceToCategory(ResourceDef[] resourceDefs){
		List<CategoryPo> categoryPos =  new ArrayList<CategoryPo>();
		if(resourceDefs!=null && resourceDefs.length>0){
			for (ResourceDef resourceDef : resourceDefs) {
				CategoryPo categoryPo = new CategoryPo();
				categoryPo.setId(resourceDef.getId());
				categoryPo.setName(resourceDef.getName());
//				if(resourceDef.getChildResourceDefs()!=null && resourceDef.getChildResourceDefs().length>0){
//					categoryPo.setChildCategorys(convertResourceToCategory(resourceDef.getChildResourceDefs()));
//				}
				categoryPos.add(categoryPo);
			}
		}
		return categoryPos;
	}
	

	@Override
	public ResourceDef getResourceDef(String id) {
		return capacityService.getResourceDefById(id);
	}


	@Override
	public List<ProfileInfoPO> getAllParentProfiles() {
		List<ProfileInfoPO> profileInfoPOs = extendProfileDao.queryAllParentProfiles();
		return profileInfoPOs;
	}



	@Override
	public List<ProfileMetricPO> getMetricsByProfile(long profileId) {
		
		return extendProfileDao.queryMetricsByProfile(profileId);
	}


	@Override
	public List<ProfileInfoPO> queryProfileInfoByResource(String resourceId) {
		
		return extendProfileDao.queryProfileInfoPOsByResourceId(resourceId);
	}

	@Override
	public boolean removeProfileMetric(long profileId,String metricId) {
		try {
			List<Long> profileIds = new ArrayList<>();
			profileIds.add(profileId);
			profileMetricDAO.removeMetricByProfileIdAndMetricId(profileIds, metricId);
			profileThresholdDAO.removeThresholdByProfileIdAndMetricId(profileIds, metricId);
			profileCache.deleteProfileMetric(profileId, metricId);
			profileCache.deleteProfileThreshold(profileId, metricId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean removeChildProfileById(long profileId) {
		if (logger.isInfoEnabled()) {
			logger.info("extended-platform removeChildProfileById start profileId=" + profileId);
		}
		try {
			List<Long> profileIds = new ArrayList<>();
			profileIds.add(profileId);
			List<ProfileInstRelationPO> profileInstRelationPOs = profileInstRelDAO.getInstRelationsByProfileIds(profileIds);
			profileDAO.removeProfileByProfileIds(profileIds);
			profileMetricDAO.removeMetricByProfileIds(profileIds);
			profileThresholdDAO.removeThresholdByProfileIds(profileIds);
			profileInstRelDAO.removeInstRelByProfileIds(profileIds);
			List<TimelineInfo> timelines = timelineService.getTimelineInfosByProfileId(profileId);
			timelineService.removeTimelineByProfileId(profileId);
			
			
			// 同步到采集器数据到数据库,更新资源实例状态
			deleteProfileToCollector(profileInstRelationPOs);
			// 删除缓存
			for (long id : profileIds) {
				removeCacheByProfileId(id, timelines);
			}
			
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("delete profile error", e);
			}
			return false;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("removeProfileById end profileId=" + profileId);
		}
		return true;
	}

	@Override
	public boolean addProfileMetricByResource(String resourceId, String metricId,long profileId) {
		ResourceDef mainResourceDef = capacityService.getResourceDefById(resourceId);
		ResourceMetricDef[] metricDefs = mainResourceDef.getMetricDefs();
		
		List<ProfileMetricPO> metricsAll = new ArrayList<ProfileMetricPO>();
		ThresholdDef[] thresholds = null;
		 Map<String, List<ProfileThreshold>> addToCacheProfileThreshold = new HashMap<String, List<ProfileThreshold>>();
		for (ResourceMetricDef metricDef : metricDefs) {
			if(metricDef.getId().equals(metricId)){
				ProfileMetricPO metric = new ProfileMetricPO();
				metric.setMetricId(metricDef.getId());
				metric.setIsUse(metricDef.isMonitor() == true ? "1" : "0");
				metric.setDictFrequencyId(metricDef.getDefaultMonitorFreq().name());
				metric.setMkId(ocProfilelibMetricSequence.next());
				metric.setProfileId(profileId);
				metric.setIsAlarm(metricDef.isAlert() == true ? "1" : "0");
				metric.setAlarmRepeat(metricDef.getDefaultFlapping());
				metricsAll.add(metric);
				String key = profileId+metricId;
				if (metricDef.getThresholdDefs() != null) {
					thresholds= metricDef.getThresholdDefs();
				}
			}
		}
		
		List<ProfileThresholdPO>  allThresholdPOs = new ArrayList<ProfileThresholdPO>();
		if (null != thresholds && thresholds.length>0) {
			for (ThresholdDef def : thresholds) {
				String stateExpression = def.getOperator() + def.getDefaultvalue();
				String enumStr = def.getState().name();
				ProfileThresholdPO thresholdPO = new ProfileThresholdPO();
				thresholdPO.setMkId(ocProfilelibThresholdSequence.next());
				thresholdPO.setMetricId(metricId);
				thresholdPO.setDictMetricState(enumStr);
//				thresholdPO.setExpressionDesc(stateExpression);
				thresholdPO.setExpressionOperator(def.getOperator().toString());
				thresholdPO.setThresholdValue(def.getDefaultvalue());
				thresholdPO.setProfileId(profileId);
				allThresholdPOs.add(thresholdPO);
				
				
			}
		}
		
		try {
			if(!CollectionUtils.isEmpty(metricsAll)){
				profileMetricDAO.insertMetrics(metricsAll);
				List<ProfileMetric> profileMetrics = tranProfileMetricPO2BOs(metricsAll);
				for (ProfileMetric profileMetric : profileMetrics) {
					profileCache.addProfileMetric(profileMetric);
				}
			}
			if(!CollectionUtils.isEmpty(allThresholdPOs)){
				profileThresholdDAO.insertThresholds(allThresholdPOs);
				
				List<ProfileThreshold> thresholdList = new ArrayList<>();
				for (ProfileThresholdPO po : allThresholdPOs) {
					thresholdList.add(transProfileThresholdPO2BO(po));
				}
				profileCache.addProfileThreshold(profileId, metricId, thresholdList);
			}
			
			return true;
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
			return false;
		}
	}
	
	private ProfileThreshold transProfileThresholdPO2BO(ProfileThresholdPO po) {
		ProfileThreshold bo = new ProfileThreshold();
		String stateStr = po.getDictMetricState();
		PerfMetricStateEnum stateEnum = PerfMetricStateEnum.valueOf(stateStr);
		bo.setPerfMetricStateEnum(stateEnum);
//		bo.setExpressionDesc(po.getExpressionDesc());
		bo.setExpressionOperator(po.getExpressionOperator());
		bo.setThresholdValue(po.getThresholdValue());
		bo.setMetricId(po.getMetricId());
		bo.setThreshold_mkId(po.getMkId());
		bo.setProfileId(po.getProfileId());
		bo.setTimelineId(po.getTimelineId());
		return bo;
	}
	
	private List<ProfileMetric> tranProfileMetricPO2BOs(List<ProfileMetricPO> metricsPOs) {
		List<ProfileMetric> bos = new ArrayList<ProfileMetric>();
		for (ProfileMetricPO metricsPO : metricsPOs) {
			ProfileMetric bo = tranProfileMetricPO2BO(metricsPO);
			bos.add(bo);
		}
		return bos;
	}
	
	private ProfileMetric tranProfileMetricPO2BO(ProfileMetricPO po) {
		ProfileMetric bo = new ProfileMetric();
		bo.setMonitor("1".equals(po.getIsUse()) ? true : false);
		bo.setMetricId(po.getMetricId());
		bo.setDictFrequencyId(po.getDictFrequencyId());
		bo.setProfileId(po.getProfileId());
		bo.setAlarm("1".equals(po.getIsAlarm()) ? true : false);
		bo.setAlarmFlapping(po.getAlarmRepeat());
		bo.setTimeLineId(po.getTimelineId());
		return bo;
	}
	
	/**
	* @Title: deleteProfileToCollector
	* @Description:删除策略后同步删除策略相关的资源并同步到DCS
	* @param profileInstRelationPOs  void
	* @throws
	*/
	private void deleteProfileToCollector(List<ProfileInstRelationPO> profileInstRelationPOs) {
		if (profileInstRelationPOs == null) {
			return;
		}
		List<ProfileChangePO> profileChangePOs = new ArrayList<>();
		Date date = new Date();
		List<Long> delInstanceIds = new ArrayList<>();
		for (ProfileInstRelationPO profileInstRelationPO : profileInstRelationPOs) {
			delInstanceIds.add(profileInstRelationPO.getInstanceId());
			// 记录存放到数据库中
			long profileChangeId = ocProfileChangeSequence.next();
			ProfileChangePO profileChangePO = new ProfileChangePO();
			profileChangePO.setChangeTime(date);
			profileChangePO.setOperateMode(ProfileChangeEnum.DELETE_PROFILE.toString());
			profileChangePO.setOperateState(0);
			profileChangePO.setProfileChangeId(profileChangeId);
			profileChangePO.setSource(String.valueOf(profileInstRelationPO.getInstanceId()));
			profileChangePO.setProfileId(profileInstRelationPO.getProfileId());
			profileChangePOs.add(profileChangePO);
			
			try {
				resourceInstanceDAO.removeResourceInstanceById(profileInstRelationPO.getInstanceId());
				instanceRelationDAO.removeRelationPOByInstanceId(profileInstRelationPO.getInstanceId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			
			profileChangeDAO.insertProfileChanges(profileChangePOs);
			
			//
			if (!CollectionUtils.isEmpty(delInstanceIds)) {
				instancePropDAO.removePropDOByInstances(delInstanceIds);
				instancePropTypeDAO.removePropTypeDOByInstances(delInstanceIds);
				//从缓存中删除资源
				for (Long delId : delInstanceIds) {
					this.instanceCache.remove(delId);
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("deleteProfileToCollector  error", e);
			}
		}
	}
	
	private void removeCacheByProfileId(long profileId, List<TimelineInfo> timelines) {
		HashSet<String> metricIds = profileCache.getMetricIdByProfileId(profileId);
		if (metricIds != null) {
			for (String metricId : metricIds) {
				profileCache.deleteProfileMetric(profileId, metricId);
				profileCache.deleteProfileThreshold(profileId, metricId);
				if (timelines != null) {
					for (TimelineInfo timeline : timelines) {
						profileCache.deleteTimelineMetric(timeline.getTimeLineId(), metricId);
						profileCache.deleteTimelineThreshold(timeline.getTimeLineId(), metricId);
					}
				}
			}
		}
	}

	@Override
	public List<ProfileInfoPO> queryProfileInfoById(long profileId) {
		return extendProfileDao.queryProfileInfoById(profileId);
	}

	@Override
	public List<ProfileInfoPO> queryProfilInfoByResourceId(String resourceId) {
		return extendProfileDao.queryProfilInfoByResourceId(resourceId);
	}

	@Override
	public List<ProfileInstancePo> queryProfileInstanceRel(long profileId) {
		return extendProfileDao.queryProfileInstanceRel(profileId);
	}

	@Override
	public List<ProfileInstancePo> queryProfileInstanceLastRel(long profileId) {
		return extendProfileDao.queryProfileInstanceLastRel(profileId);
	}

	@Override
	public List<ProfileInstancePo> queryProfileInstanceRelByResourceId(String resourceId) {
		return extendProfileDao.queryProfileInstanceRelByResourceId(resourceId);
	}

	@Override
	public List<ProfileInstancePo> queryProfileInstanceLastRelByResourceId(String resourceId) {
		return extendProfileDao.queryProfileInstanceLastRelByResourceId(resourceId);
	}

	@Override
	public int deleteProfileInstanceRel(long profileId) {
		return extendProfileDao.deleteProfileInstanceRel(profileId);
	}

	@Override
	public int deleteProfileInstanceLastRel(long profileId) {
		return  extendProfileDao.deleteProfileInstanceLastRel(profileId);
	}

	@Override
	public int deleteProfileInstanceRelByResourceId(String resourceId) {
		return  extendProfileDao.deleteProfileInstanceRelByResourceId(resourceId);
	}

	@Override
	public int deleteProfileInstanceLastRelByResourceId(String resourceId) {
		return  extendProfileDao.deleteProfileInstanceLastRelByResourceId(resourceId);
	}

	@Override
	public boolean removeProfileByIds(List<Long> profileIds) {
		if (logger.isInfoEnabled()) {
			logger.info("extended-platform removeChildProfileById start profileId=" + profileIds);
		}
		
		if(CollectionUtils.isEmpty(profileIds)){
			return false;
		}
		try {
			List<ProfileInstRelationPO> profileInstRelationPOs = profileInstRelDAO.getInstRelationsByProfileIds(profileIds);
			profileDAO.removeProfileByProfileIds(profileIds);
			profileMetricDAO.removeMetricByProfileIds(profileIds);
			profileThresholdDAO.removeThresholdByProfileIds(profileIds);
			profileInstRelDAO.removeInstRelByProfileIds(profileIds);
			
			for(Long profileId:profileIds){
				List<TimelineInfo> timelines = timelineService.getTimelineInfosByProfileId(profileId);
				timelineService.removeTimelineByProfileId(profileId);
				removeCacheByProfileId(profileId, timelines);
			}
			
			// 同步到采集器数据到数据库,更新资源实例状态
			deleteProfileToCollector(profileInstRelationPOs);
			
			
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("delete profile error", e);
			}
			return false;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("removeProfileById end profileId=" + profileIds);
		}
		return true;
	}
	
}
