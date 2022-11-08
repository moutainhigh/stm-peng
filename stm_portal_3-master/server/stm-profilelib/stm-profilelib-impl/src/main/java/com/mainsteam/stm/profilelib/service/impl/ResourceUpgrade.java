package com.mainsteam.stm.profilelib.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.caplib.resource.ThresholdDef;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.profilelib.TimelineService;
import com.mainsteam.stm.profilelib.obj.Timeline;
import com.mainsteam.stm.profilelib.po.ProfileInfoPO;
import com.mainsteam.stm.profilelib.po.ProfileMetricPO;
import com.mainsteam.stm.profilelib.po.ProfileThresholdPO;

public class ResourceUpgrade {
	private static final Log logger = LogFactory
			.getLog(ResourceUpgrade.class);
	private CapacityService capacityService;
	private ProfileServiceImpl profileService;
	private TimelineService timelineService;
	public ResourceUpgrade(ProfileServiceImpl profileService){
		this.profileService = profileService;
		this.capacityService = profileService.getCapacityService();
		this.timelineService = profileService.getTimelineService();
	}
	/**
	 * 模型文件升级方案：
	 * 1：父模型增加，不参与升级
	 * 2：父模型删除，不考虑。子模型删除需要更新策略状态，策略不可用。取消当前模型以及子模型监控的资源实例状态，删除模型对应的资源实例
	 */
	public boolean load() throws Exception{
	
		boolean upgrade = false;
		//key resourceId value: metricId
		HashMap<String,HashSet<String>> oldResourceIdAndMetrics = new HashMap<>(500);
		//key resourceId,metricId   value Threshold  只需要通过key标识是否有阈值即可
	    HashMap<String,Object> oldMetricAndThresolds = new HashMap<>(3);
		//key profileId,value resourceId
		HashMap<Long,String> profileIdAndResourceId = null;
		//key resourceId value profileId
		HashMap<String,List<Long>> resourceIdAndprofileId = null;
		//子 resourceId 
		HashSet<String>  oldChildResourceId = new HashSet<>();
		//存放新模型，key: resourceId,value:[key:metricId,ResourceMetricDef]
		HashMap<String,Map<String,ResourceMetricDef>> newResourceIdAndMetrics = null;
		//子模型删除
		HashSet<String> deleteResource = new HashSet<>(100);
		// key： rsourceId, List<String> 指标Id
		Map<String,List<String>> addMetrices = new HashMap<>();
		// key： rsourceId, List<String> 指标Id
		Map<String,List<String>> addThresolds = new HashMap<>();
		
		Map<String,HashSet<String>> deleteMetrics = new HashMap<>();
		//key: profileId, value : timelineId
		Map<Long,List<Long>> profileAndTimeline = new HashMap<Long, List<Long>>(20);
		try {
			List<ProfileMetricPO> allProfileMetric = profileService.getProfileMetricDAO().getAllMetric();
			if(allProfileMetric == null || allProfileMetric.isEmpty()){
				return upgrade;
			}
			List<ProfileInfoPO> allProfiles = profileService.getProfileDAO().getAllProfilePos();
			if(allProfiles == null || allProfiles.isEmpty()){
				return upgrade;
			}
			//信息指标如果修改成性能指标，需要同步添加到DB中
			List<ProfileThresholdPO> allThrsholdPOs = profileService.getProfileThresholdDAO().getAllThreshold();
			
			List<Timeline> allTimelines = timelineService.getTimelines();
			profileIdAndResourceId = new HashMap<>(allProfiles.size());
			resourceIdAndprofileId = new HashMap<>(500);
			//存放策略基线的信息
			if(allTimelines != null && !allTimelines.isEmpty()){
				for (Timeline timeline : allTimelines) {
					long timelineId = timeline.getTimelineInfo().getTimeLineId();
					long timelineProfileId = timeline.getTimelineInfo().getProfileId();
					List<Long> timelineIds = profileAndTimeline.get(timelineProfileId);
					if(timelineIds == null){
						timelineIds = new ArrayList<Long>();
						profileAndTimeline.put(timelineProfileId, timelineIds);
					}
					timelineIds.add(timelineId);
				}
			}
			//构造策略，模型文件关系,阈值关系
			for (ProfileInfoPO profileInfoPO : allProfiles) {
				profileIdAndResourceId.put(profileInfoPO.getProfileId(), profileInfoPO.getResourceId());
				List<Long> profileIds = resourceIdAndprofileId.get(profileInfoPO.getResourceId());
				if(profileIds == null){
					profileIds = new ArrayList<Long>();
					resourceIdAndprofileId.put(profileInfoPO.getResourceId(), profileIds);
				}
				profileIds.add(profileInfoPO.getProfileId());
				if(profileInfoPO.getParentProfileId() != null){
					oldChildResourceId.add(profileInfoPO.getResourceId());
				}
			}
			for (ProfileMetricPO profileMetricPO : allProfileMetric) {
				String resourceId = profileIdAndResourceId.get(profileMetricPO.getProfileId());
				if(resourceId == null){
					if(logger.isWarnEnabled()){
						StringBuilder bb = new StringBuilder();
						bb.append("profileId=").append(profileMetricPO.getProfileId());
						bb.append(" resourceId not found!");
						logger.warn(bb);
					}
					continue;
				}
				HashSet<String> profileMetrics = oldResourceIdAndMetrics.get(resourceId);
				if(profileMetrics == null){
					profileMetrics = new HashSet<>(50);
					oldResourceIdAndMetrics.put(resourceId,profileMetrics);
				}
				profileMetrics.add(profileMetricPO.getMetricId());
			}
			if(allThrsholdPOs != null){
				for (ProfileThresholdPO profileThresholdPO : allThrsholdPOs) {
					String resourceId = profileIdAndResourceId.get(profileThresholdPO.getProfileId());
					if(resourceId == null){
						continue;
					}
					String key = resourceId + ",," +  profileThresholdPO.getMetricId();
					if(!oldMetricAndThresolds.containsKey(key)){
						oldMetricAndThresolds.put(key,null);
					}
				}
			}
			//包含父与子的模型集合
			List<ResourceDef> resourceDefs = capacityService.getResourceDefList();
			newResourceIdAndMetrics = new HashMap<>(resourceDefs.size());
			Map<String,Object> newChildrenResourcerId = new HashMap<>();
			for (ResourceDef resourceDef : resourceDefs) {
				if(resourceDef.getParentResourceDef() != null){
					//父模型不做考虑，用于计算子模型删除情况
					newChildrenResourcerId.put(resourceDef.getId(),null);
				}
				//父与子模型都要计算指标的增减
				HashSet<String> oldMetricIds =  oldResourceIdAndMetrics.get(resourceDef.getId());
				if(oldMetricIds != null){
					ResourceMetricDef[] resourceMetricDef = resourceDef.getMetricDefs();
					HashMap<String,ResourceMetricDef> metricMap = new HashMap<>();
					newResourceIdAndMetrics.put(resourceDef.getId(), metricMap);
					List<String> newMetricIds = new ArrayList<String>(resourceMetricDef.length); 
					for (ResourceMetricDef newResourceMetricDef : resourceMetricDef) {
						newMetricIds.add(newResourceMetricDef.getId());
						metricMap.put(newResourceMetricDef.getId(), newResourceMetricDef);
					}
					//取交集
					Collection<?> intersection = CollectionUtils.intersection(oldMetricIds,newMetricIds);
					//需要添加的指标
					newMetricIds.removeAll(intersection);
					if(!newMetricIds.isEmpty()){
						addMetrices.put(resourceDef.getId(), newMetricIds);
					}
					//需要删除的指标
					oldMetricIds.removeAll(intersection);
					if(!oldMetricIds.isEmpty()){
						deleteMetrics.put(resourceDef.getId(), oldMetricIds);
					}
					//需要添加阈值（直接判断是交集的指标，并且是性能指标）
					for (Object tempMetricId : intersection) {
						String metricId = tempMetricId.toString();
						ResourceMetricDef r = capacityService.getResourceMetricDef(resourceDef.getId(), metricId);
						if(r == null){
							continue;
						}
						String findKey = resourceDef.getId() + ",," + metricId;
						//DB指标有阈值不用处理。 
						if(!oldMetricAndThresolds.containsKey(findKey)){
							ThresholdDef[] thresholdDefs = r.getThresholdDefs();
							if(thresholdDefs != null && thresholdDefs.length > 0){
								List<String> tempMetricIds = addThresolds.get(resourceDef.getId());
								if(tempMetricIds == null){
									tempMetricIds = new ArrayList<String>();
									addThresolds.put(resourceDef.getId(), tempMetricIds);
								}
								tempMetricIds.add(metricId);
							}
						}
					}
				}
			}
			//计算子模型删除的情况-- 循环遍历数据库中存在的子模型
			for (String resourceId : oldChildResourceId) {
				//子模型
				if(!newChildrenResourcerId.containsKey(resourceId)){
					deleteResource.add(resourceId);
				}
			}
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("",e);
			}
			throw e;
		}
		StringBuilder bb = new StringBuilder(1000);
		/*
		 * 执行更新模型操作
		 */
		//删除子模型 1：物理删除模型对应的资源实例  2：删除改模型策略绑定资源实例关系 3：更新策略中模型状态不可用
//		if(!deleteResource.isEmpty()){
//			upgrade = true;
//			try {
//				//删除资源
//				List<Long> deleteIds = profileService.getResourceInstanceExtendService().deleteResourceInstanceByResourceIds(deleteResource);
//				if(deleteIds != null && !deleteIds.isEmpty()){
//					//直接删除策略，基线，指标，阈值,已经绑定关系
//					profileService.deleteProfileByResourceIdAndInstanceId(deleteIds, deleteResource);
//				}
//			} catch (InstancelibException e) {
//				if(logger.isErrorEnabled()){
//					logger.error("",e);
//				}
//				throw e;
//			}
//			if(logger.isInfoEnabled()){
//				bb.append("\n deleteResource list: resourceId=").append(deleteResource);
//			}
//		}
		//删除指标更新操作
//		if(!deleteMetrics.isEmpty()){
//			upgrade = true;
//			if(logger.isInfoEnabled()){
//				bb.append("delete metric list: \n");
//			}
//			for (Entry<String, HashSet<String>> item : deleteMetrics.entrySet()) {
//				String resourceId = item.getKey();
//				List<Long> profileIds = resourceIdAndprofileId.get(resourceId);
//				if(logger.isInfoEnabled()){
//					bb.append("resourceId=").append(item.getKey()).append(" ");
//					bb.append("delete: ");
//				}
//				for (String metricId : item.getValue()) {
//					try {
//						profileService.getProfileMetricDAO().removeMetricByProfileIdAndMetricId(profileIds, metricId);
//						profileService.getProfileThresholdDAO().removeThresholdByProfileIdAndMetricId(profileIds, metricId);
//					} catch (Exception e) {
//						if(logger.isErrorEnabled()){
//							logger.error("upgrade removeMetric",e);
//						}
//					}
//					if(logger.isInfoEnabled()){
//						bb.append(metricId).append(",");
//					}
//				}
//				if(logger.isInfoEnabled()){
//					bb.append("\n");
//				}
//			}
//		}
		//添加指标更新操作
		if(!addMetrices.isEmpty()){
			upgrade = true;
			if(logger.isInfoEnabled()){
				bb.append("\n add metric list: \n");
			}
			List<ProfileMetricPO> addProfileMetricPOs = new ArrayList<>(50);
			List<ProfileThresholdPO> addProfileThresholdPOs = new ArrayList<>(150);
			for (Entry<String, List<String>> item : addMetrices.entrySet()) {
				String resourceId = item.getKey();
				List<Long> profileIds = resourceIdAndprofileId.get(resourceId);
				if(logger.isInfoEnabled()){
					bb.append("resourceId=").append(item.getKey()).append(" ");
					bb.append("add ");
				}
				Map<String, ResourceMetricDef> mapMetricDef = newResourceIdAndMetrics.get(resourceId);
				for (String metricId : item.getValue()) {
					ResourceMetricDef def = mapMetricDef.get(metricId);
					ThresholdDef[] thresholdDefs = def.getThresholdDefs();
					for (long profileId : profileIds) {
						//指标
						ProfileMetricPO metricPO = metricConvertToPO(profileId,0,metricId,def,profileService,timelineService);
						addProfileMetricPOs.add(metricPO);
						if(thresholdDefs != null){
							//阈值
							for (ThresholdDef thresholdDef : thresholdDefs) {
								ProfileThresholdPO thresholdPO = thresholdConvertToPO(profileId,0,metricId,thresholdDef,profileService,timelineService);
								addProfileThresholdPOs.add(thresholdPO);
							}
						}
						//基线
						if(profileAndTimeline.containsKey(profileId)){
							for (List<Long> timelineIds : profileAndTimeline.values()) {
								for (long timelineId : timelineIds) {
									//指标
									ProfileMetricPO timelineMetricPO = metricConvertToPO(profileId,timelineId,metricId,def,profileService,timelineService);
									addProfileMetricPOs.add(timelineMetricPO);
									if(thresholdDefs != null){
										//阈值
										for (ThresholdDef thresholdDef : thresholdDefs) {
											ProfileThresholdPO timelineThresholdPO = thresholdConvertToPO(profileId,timelineId,metricId,thresholdDef,profileService,timelineService);
											addProfileThresholdPOs.add(timelineThresholdPO);
										}
									}
								}
							}
						}
					}
					if(logger.isInfoEnabled()){
						bb.append("metricId=").append(metricId).append("  ");
					}
				}
				if(logger.isInfoEnabled()){
					bb.append("\n");
				}
			}
			try {
				if(!addProfileMetricPOs.isEmpty()){
					profileService.getProfileMetricDAO().insertMetrics(addProfileMetricPOs);
				}
				if(!addProfileThresholdPOs.isEmpty()){
					profileService.getProfileThresholdDAO().insertThresholds(addProfileThresholdPOs);
				}
			} catch (InstancelibException e) {
				if(logger.isErrorEnabled()){
					logger.error("",e);
				}
				throw e;
			}
		}
		//添加指标阈值操作
		if(!addThresolds.isEmpty()){
			upgrade = true;
			if(logger.isInfoEnabled()){
				bb.append("\n add metric thresold list: \n");
			}
			List<ProfileThresholdPO> addProfileThresholdPOs = new ArrayList<>(150);
			for (Entry<String, List<String>> item : addThresolds.entrySet()) {
				String resourceId = item.getKey();
				List<Long> profileIds = resourceIdAndprofileId.get(resourceId);
				if(logger.isInfoEnabled()){
					bb.append("resourceId=").append(item.getKey()).append(" ");
					bb.append("add ");
				}
				Map<String, ResourceMetricDef> mapMetricDef = newResourceIdAndMetrics.get(resourceId);
				for (String metricId : item.getValue()) {
					ResourceMetricDef def = mapMetricDef.get(metricId);
					ThresholdDef[] thresholdDefs = def.getThresholdDefs();
					for (long profileId : profileIds) {
						if(thresholdDefs != null){
							//阈值
							for (ThresholdDef thresholdDef : thresholdDefs) {
								ProfileThresholdPO thresholdPO = thresholdConvertToPO(profileId,0,metricId,thresholdDef,profileService,timelineService);
								addProfileThresholdPOs.add(thresholdPO);
							}
						}
						//基线
						if(profileAndTimeline.containsKey(profileId)){
							for (List<Long> timelineIds : profileAndTimeline.values()) {
								for (long timelineId : timelineIds) {
									if(thresholdDefs != null){
										//阈值
										for (ThresholdDef thresholdDef : thresholdDefs) {
											ProfileThresholdPO timelineThresholdPO = thresholdConvertToPO(profileId,timelineId,metricId,thresholdDef,profileService,timelineService);
											addProfileThresholdPOs.add(timelineThresholdPO);
										}
									}
								}
							}
						}
					}
					if(logger.isInfoEnabled()){
						bb.append("metricId=").append(metricId).append("'s Threshold").append(" ");
					}
				}
				if(logger.isInfoEnabled()){
					bb.append("\n");
				}
			}
			try {
				if(!addProfileThresholdPOs.isEmpty()){
					profileService.getProfileThresholdDAO().insertThresholds(addProfileThresholdPOs);
				}
			} catch (InstancelibException e) {
				if(logger.isErrorEnabled()){
					logger.error("",e);
				}
				throw e;
			}
		}
		if(logger.isInfoEnabled()){
			if(bb.length() > 0){
				logger.info(bb);
			}
		}
		return upgrade;
	}
	
	private ProfileMetricPO metricConvertToPO(long profileId,long timelineId,String metricId,ResourceMetricDef def,ProfileServiceImpl profileService,TimelineService timelineService){
		ProfileMetricPO metricPO = new ProfileMetricPO();
		metricPO.setProfileId(profileId);
		metricPO.setMkId(profileService.getOcProfilelibMetricSequence().next());
		metricPO.setMetricId(metricId);
		metricPO.setIsUse(def.isMonitor()?"1":"0");
		metricPO.setIsAlarm(def.isAlert()?"1":"0");
		metricPO.setDictFrequencyId(def.getDefaultMonitorFreq().name());
		metricPO.setAlarmRepeat(def.getDefaultFlapping());
		metricPO.setTimelineId(timelineId);
		return metricPO;
	}
	
	private ProfileThresholdPO thresholdConvertToPO(long profileId,long timelineId,String metricId,ThresholdDef thresholdDef,ProfileServiceImpl profileService,TimelineService timelineService){
		String enumStr = thresholdDef.getState().name();
		ProfileThresholdPO thresholdPO = new ProfileThresholdPO();
		thresholdPO.setMkId(profileService.getOcProfilelibThresholdSequence().next());
		thresholdPO.setMetricId(metricId);
		thresholdPO.setDictMetricState(enumStr);
		thresholdPO.setExpressionDesc(thresholdDef.getThresholdExpression());
		if(thresholdDef.getOperator() != null){
			thresholdPO.setExpressionOperator(thresholdDef.getOperator()
					.toString());
		}
		thresholdPO.setThresholdValue(thresholdDef.getDefaultvalue());
		thresholdPO.setProfileId(profileId);
		thresholdPO.setTimelineId(timelineId);
		return thresholdPO;
	}
	
}
