package com.mainsteam.stm.portal.resource.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.FrequentEnum;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.portal.resource.api.ITimelineApi;
import com.mainsteam.stm.portal.resource.bo.MetricFrequentBo;
import com.mainsteam.stm.portal.resource.bo.MetricSettingBo;
import com.mainsteam.stm.portal.resource.bo.ProfileMetricBo;
import com.mainsteam.stm.portal.resource.bo.TimeLineBo;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.TimelineService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.MetricSetting;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.profilelib.obj.Threshold;
import com.mainsteam.stm.profilelib.obj.Timeline;
import com.mainsteam.stm.profilelib.obj.TimelineInfo;

public class TimelineImpl implements ITimelineApi{
	
	@Resource
	private ProfileService  profileService;
	
	@Resource
	private TimelineService timelineService;
	
	@Resource
	private CapacityService capacityService;
	

	@Override
	public TimeLineBo getTimeline(long timelineId,String resourceId) throws ProfilelibException {
		TimeLineBo bo=new TimeLineBo();
		
		Timeline timeline=timelineService.getTimelinesById(timelineId);

		if(timeline!=null){
			//获取策略的基本信息
			bo=timeLineToBo(timeline,resourceId);
		}

		return bo;
	}

	@Override
	public List<TimeLineBo> getTimelinesByProfileId(long profileId,String resourceId) throws ProfilelibException {
		List<TimeLineBo> bos=new ArrayList<TimeLineBo>();
		List<Timeline> timelines=timelineService.getTimelinesByProfileId(profileId);
		bos=timeLinesToBos(timelines,resourceId);
		
//		ProfileInfo profileInfo=profileService.getProfileBasicInfoByProfileId(profileId);	
//		if(profileInfo!=null){
//			String resourceId=profileInfo.getResourceId();
//		}

		return bos ;
	}

	@Override
	public long addTimeline(TimeLineBo timelineBo) throws ProfilelibException{
		
		Timeline timeline=boToTimeLine(timelineBo);
		
		long timelineId=timelineService.insertTimeline(timeline);
		
		return timelineId;
	}

	@Override
	public void addTimelineList(List<TimeLineBo> timelines) throws ProfilelibException {
		timelineService.insertTimelines(bosToTimeLines(timelines));
	}

	@Override
	public void updateTimeline(TimeLineBo timelineBoOld,TimeLineBo timelineBoCurrent) throws ProfilelibException {
		long profileId=timelineBoOld.getProfileId();
		long timelineId=timelineBoOld.getTimeLineId();
		
		Timeline timelineOld=boToTimeLine(timelineBoOld);
		Timeline timelineCurrent=boToTimeLine(timelineBoCurrent);
		
		//基础信息
		TimelineInfo infoOld=timelineOld.getTimelineInfo();
		TimelineInfo infoNew=timelineCurrent.getTimelineInfo();
		
		//修改基线时间区间和Name，需要修改 updateTimelineInfo
		if(!infoOld.equals(infoNew)){
			timelineService.updateTimelineInfo(infoNew);
		}
		//指标阈值
		Map<String,ProfileMetric> profileMetricMap=new HashMap<String,ProfileMetric>();
		List<ProfileMetric> metricsOld=timelineOld.getMetricSetting().getMetrics();
		for(ProfileMetric profileMetric:metricsOld){
			profileMetricMap.put(profileMetric.getMetricId(), profileMetric);
		}
		//告警
		Map<String,Boolean> alarmMap=new HashMap<String,Boolean>();
		//监控
		Map<String,Boolean> monitorMap=new HashMap<String,Boolean>();
		//flapping 次数
		Map<String,Integer> flappingMap=new HashMap<String,Integer>();
		//采集频度
		Map<String,String> frequencyMap=new HashMap<String,String>();
		
		List<ProfileMetric> metricsNew=timelineCurrent.getMetricSetting().getMetrics();
		for(ProfileMetric newMetric:metricsNew){
			String metricId=newMetric.getMetricId();
			
			ProfileMetric oldMetric=profileMetricMap.get(metricId);
			//Alarm
			if(oldMetric.isAlarm()!=newMetric.isAlarm()){
				alarmMap.put(metricId, newMetric.isAlarm());
			}
			//Monitor
			if(oldMetric.isMonitor()!=newMetric.isMonitor()){
				monitorMap.put(metricId, newMetric.isMonitor());
			}
			//AlarmFlapping
			if(oldMetric.getAlarmFlapping()!=newMetric.getAlarmFlapping()){
				flappingMap.put(metricId, newMetric.getAlarmFlapping());
			}
			//Monitor
			if(!oldMetric.getDictFrequencyId().equals(newMetric.getDictFrequencyId())){
				frequencyMap.put(metricId, newMetric.getDictFrequencyId());
			}

		}
		
		if(alarmMap.size()>0){
			timelineService.updateTimelineMetricAlarm(profileId, timelineId, alarmMap);
		}
		if(monitorMap.size()>0){
			timelineService.updateTimelineMetricMonitor(profileId, timelineId, monitorMap);		
		}
		if(flappingMap.size()>0){
			timelineService.updateTimelineMetricflapping(profileId, timelineId, flappingMap);
		}
		if(frequencyMap.size()>0){
			timelineService.updateTimelineMetricFrequency(profileId, timelineId, frequencyMap);
		}
	
	}

	@Override
	public void removeTimelineId(long timelineId) throws ProfilelibException{
		timelineService.removeTimelineByTimelineId(timelineId);
	}

	/**
	 * TimeLine 转换 timeLineBo
	 * @param timeLine
	 * @return
	 * @throws ProfilelibException 
	 */
	private TimeLineBo timeLineToBo(Timeline timeLine,String resourceId){
		
		TimeLineBo bo=new TimeLineBo();
		TimelineInfo info=timeLine.getTimelineInfo();

		BeanUtils.copyProperties(info, bo);
		
		//策略的指标列表
		List<ProfileMetric> profileMetrics=timeLine.getMetricSetting().getMetrics();
		//可用性指标
		List<ProfileMetricBo> availabilityMetricList = new ArrayList<ProfileMetricBo>();

		//信息指标
		List<ProfileMetricBo> informationMetricList = new ArrayList<ProfileMetricBo>();
		
		//性能指标
		List<ProfileMetricBo> performanceMetricList = new ArrayList<ProfileMetricBo>();
		
		List<ProfileMetricBo> profileMetricBoList=new ArrayList<ProfileMetricBo>();
		
		for(ProfileMetric profileMetric:profileMetrics){
			String metricId=profileMetric.getMetricId();
			
			ResourceMetricDef resourceMetricDef=capacityService.getResourceMetricDef(resourceId, metricId);
			
			if(resourceMetricDef == null){
				continue;
			}
			
			if(!resourceMetricDef.isDisplay()){
				continue;
			}
			
			//获取到指标名称
			String metricName=resourceMetricDef.getName();
			//获取到指标类型
			MetricTypeEnum metricTypeEnum = resourceMetricDef.getMetricType();
			
			String unit = resourceMetricDef.getUnit();
			
			if(metricTypeEnum == MetricTypeEnum.AvailabilityMetric){
				
				List<MetricFrequentBo> metricFrequentBoList = new ArrayList<MetricFrequentBo>();
				
				//遍历出该指标支持的频度
				FrequentEnum[] frequents = resourceMetricDef.getSupportMonitorFreq();
				for(FrequentEnum singleFrequent : frequents){
					MetricFrequentBo frequentBo = new MetricFrequentBo();
					frequentBo.setId(singleFrequent.name());
					frequentBo.setName(FrequentEnum.valueOf(singleFrequent.name()).toString());
					metricFrequentBoList.add(frequentBo);
				}
				
				//显示可用性指标
				ProfileMetricBo profileMetricBo=new ProfileMetricBo();
				BeanUtils.copyProperties(profileMetric, profileMetricBo);
				profileMetricBo.setMetricName(metricName);
				profileMetricBo.setMetricTypeEnum(metricTypeEnum);
				profileMetricBo.setSupportFrequentList(metricFrequentBoList);
				profileMetricBo.setDisplayOrder(Long.parseLong(resourceMetricDef.getDisplayOrder()));
				profileMetricBo.setUnit(unit);
				
				availabilityMetricList.add(profileMetricBo);
				
			}
			
			if(metricTypeEnum == MetricTypeEnum.InformationMetric){
				
				List<MetricFrequentBo> metricFrequentBoList = new ArrayList<MetricFrequentBo>();
				
				//遍历出该指标支持的频度
				FrequentEnum[] frequents = resourceMetricDef.getSupportMonitorFreq();
				for(FrequentEnum singleFrequent : frequents){
					MetricFrequentBo frequentBo = new MetricFrequentBo();
					frequentBo.setId(singleFrequent.name());
					frequentBo.setName(FrequentEnum.valueOf(singleFrequent.name()).toString());
					metricFrequentBoList.add(frequentBo);
				}
				
				//显示可用性指标
				ProfileMetricBo profileMetricBo=new ProfileMetricBo();
				BeanUtils.copyProperties(profileMetric, profileMetricBo);
				profileMetricBo.setMetricName(metricName);
				profileMetricBo.setMetricTypeEnum(metricTypeEnum);
				profileMetricBo.setSupportFrequentList(metricFrequentBoList);
				profileMetricBo.setDisplayOrder(Long.parseLong(resourceMetricDef.getDisplayOrder()));
				profileMetricBo.setUnit(unit);
				
				informationMetricList.add(profileMetricBo);
				
			}
			
			if(metricTypeEnum == MetricTypeEnum.PerformanceMetric){
				
				List<MetricFrequentBo> metricFrequentBoList = new ArrayList<MetricFrequentBo>();
				
				//遍历出该指标支持的频度
				FrequentEnum[] frequents = resourceMetricDef.getSupportMonitorFreq();
				for(FrequentEnum singleFrequent : frequents){
					MetricFrequentBo frequentBo = new MetricFrequentBo();
					frequentBo.setId(singleFrequent.name());
					frequentBo.setName(FrequentEnum.valueOf(singleFrequent.name()).toString());
					metricFrequentBoList.add(frequentBo);
				}
				
				//显示性能指标
				ProfileMetricBo profileMetricBo=new ProfileMetricBo();
				BeanUtils.copyProperties(profileMetric, profileMetricBo);
				profileMetricBo.setMetricName(metricName);
				profileMetricBo.setMetricTypeEnum(metricTypeEnum);
				profileMetricBo.setSupportFrequentList(metricFrequentBoList);
				profileMetricBo.setDisplayOrder(Long.parseLong(resourceMetricDef.getDisplayOrder()));
				profileMetricBo.setUnit(unit);
				
				performanceMetricList.add(profileMetricBo);
				
			}
			
		}
		
		Collections.sort(availabilityMetricList, new Comparator<ProfileMetricBo>() {

			@Override
			public int compare(ProfileMetricBo o1, ProfileMetricBo o2) {
				// TODO Auto-generated method stub
				if(o1.getDisplayOrder() > o2.getDisplayOrder()){
					return 1;
				}
				
				if(o1.getDisplayOrder() < o2.getDisplayOrder()){
					return -1;
				}
				
				return 0;
				
			}
			
		});
		
		Collections.sort(informationMetricList, new Comparator<ProfileMetricBo>() {

			@Override
			public int compare(ProfileMetricBo o1, ProfileMetricBo o2) {
				// TODO Auto-generated method stub
				if(o1.getDisplayOrder() > o2.getDisplayOrder()){
					return 1;
				}
				
				if(o1.getDisplayOrder() < o2.getDisplayOrder()){
					return -1;
				}
				
				return 0;
				
			}
			
		});
		
		Collections.sort(performanceMetricList, new Comparator<ProfileMetricBo>() {

			@Override
			public int compare(ProfileMetricBo o1, ProfileMetricBo o2) {
				// TODO Auto-generated method stub
				if(o1.getDisplayOrder() > o2.getDisplayOrder()){
					return 1;
				}
				
				if(o1.getDisplayOrder() < o2.getDisplayOrder()){
					return -1;
				}
				
				return 0;
				
			}
			
		});
		
		profileMetricBoList.addAll(availabilityMetricList);
		profileMetricBoList.addAll(informationMetricList);
		profileMetricBoList.addAll(performanceMetricList);

		MetricSettingBo metricSettingBo=new MetricSettingBo();
		metricSettingBo.setMetrics(profileMetricBoList);
		
		bo.setMetricSetting(metricSettingBo);
		
		return bo;
	}
	
	/**
	 * TimeLineBo 转换 timeLine
	 * @param timeLineBo
	 * @return
	 */
	private Timeline boToTimeLine(TimeLineBo timeLineBo){
		Timeline timeline=new Timeline();

		TimelineInfo timelineInfo=new TimelineInfo();
		BeanUtils.copyProperties(timeLineBo, timelineInfo);
		
		MetricSettingBo mBo=timeLineBo.getMetricSetting();
		MetricSetting metricSetting=boTometricSetting(mBo);
		
		timeline.setTimelineInfo(timelineInfo);
		timeline.setMetricSetting(metricSetting);
		return timeline;
	}
	
	/**
	 * TimeLine集合 转换 timeLineBo集合
	 * @param timeLine
	 * @return
	 */
	private List<TimeLineBo> timeLinesToBos(List<Timeline> timeLines,String resourceId){
		List<TimeLineBo> list=new ArrayList<TimeLineBo>();
		
		for(Timeline timeline:timeLines){
			list.add(timeLineToBo(timeline,resourceId));	
		}
		
		return list;
	}
	
	/**
	 * TimeLineBo集合 转换 timeLine集合
	 * @param timeLineBo
	 * @return
	 */
	private List<Timeline> bosToTimeLines(List<TimeLineBo> timeLineBos){
		List<Timeline> list=new ArrayList<Timeline>();
		for(TimeLineBo bo:timeLineBos){
			list.add(boToTimeLine(bo));	
		}
		return list;
	}
	

	/**
	 * 将MetricSetting转为MetricSettingBo
	 * @return
	 */
	private MetricSettingBo metricSettingToBo(MetricSetting metricSetting,String resourceId){
		
		//策略的指标列表
		List<ProfileMetric> profileMetrics=metricSetting.getMetrics();
		
		//可用性指标
		List<ProfileMetricBo> availabilityMeticList = new ArrayList<ProfileMetricBo>();
		
		//性能指标
		List<ProfileMetricBo> performanceMeticList = new ArrayList<ProfileMetricBo>();
		
		List<ProfileMetricBo> profileMetricBoList=new ArrayList<ProfileMetricBo>();
		
		for(ProfileMetric profileMetric:profileMetrics){
			String metricId=profileMetric.getMetricId();
			
			ResourceMetricDef resourceMetricDef=capacityService.getResourceMetricDef(resourceId, metricId);
			
			if(resourceMetricDef==null){
				continue;
			}
			
			if(!resourceMetricDef.isDisplay()){
				continue;
			}
			
			//获取到指标名称
			String metricName=resourceMetricDef.getName();
			//获取到指标类型
			MetricTypeEnum metricTypeEnum = resourceMetricDef.getMetricType();
			
			if(metricTypeEnum == MetricTypeEnum.AvailabilityMetric){
				
				//显示可用性指标
				ProfileMetricBo profileMetricBo=new ProfileMetricBo();
				BeanUtils.copyProperties(profileMetric, profileMetricBo);
				profileMetricBo.setMetricName(metricName);
				profileMetricBo.setMetricTypeEnum(metricTypeEnum);
				
				availabilityMeticList.add(profileMetricBo);
			}
			
			if(metricTypeEnum == MetricTypeEnum.PerformanceMetric){
				
				//显示性能指标
				ProfileMetricBo profileMetricBo=new ProfileMetricBo();
				BeanUtils.copyProperties(profileMetric, profileMetricBo);
				profileMetricBo.setMetricName(metricName);
				profileMetricBo.setMetricTypeEnum(metricTypeEnum);
				
				performanceMeticList.add(profileMetricBo);
				
			}
			
		}
		
		profileMetricBoList.addAll(availabilityMeticList);
		profileMetricBoList.addAll(performanceMeticList);

		MetricSettingBo metricSettingBo=new MetricSettingBo();
		metricSettingBo.setMetrics(profileMetricBoList);
		
		return metricSettingBo;
		
	}
	
	/**
	 * 将MetricSettingBo 转为MetricSetting
	 * @param bo
	 * @return
	 */
	public MetricSetting boTometricSetting(MetricSettingBo bo){
		MetricSetting setting=new MetricSetting();
		
		List<ProfileMetric> metrics=new ArrayList<ProfileMetric>();
		
		List<ProfileMetricBo> profileMetricBoList=bo.getMetrics();
		
		for(ProfileMetricBo metricBo:profileMetricBoList){
			metrics.add(metricBo);
		}
		
		setting.setMetrics(metrics);
		
		return setting;
	}
	
	
	public ProfileService getProfileService()  {
		return profileService;
	}

	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}

	public TimelineService getTimelineService() {
		return timelineService;
	}

	public void setTimelineService(TimelineService timelineService) {
		this.timelineService = timelineService;
	}

	public CapacityService getCapacityService() {
		return capacityService;
	}

	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}
	

}
