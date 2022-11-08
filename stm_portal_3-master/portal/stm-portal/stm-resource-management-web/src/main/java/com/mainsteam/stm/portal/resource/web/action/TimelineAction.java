package com.mainsteam.stm.portal.resource.web.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.caplib.dict.PerfMetricStateEnum;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.resource.api.ITimelineApi;
import com.mainsteam.stm.portal.resource.api.ProfileApi;
import com.mainsteam.stm.portal.resource.api.StrategyDetailApi;
import com.mainsteam.stm.portal.resource.bo.MetricSettingBo;
import com.mainsteam.stm.portal.resource.bo.ProfileMetricBo;
import com.mainsteam.stm.portal.resource.bo.TimeLineBo;
import com.mainsteam.stm.portal.resource.web.vo.ProfileMetricPageVo;
import com.mainsteam.stm.portal.resource.web.vo.TimelineVo;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.profilelib.objenum.TimelineTypeEnum;



@Controller
@RequestMapping("/portal/resource/timeline")
public class TimelineAction extends BaseAction{
	
	private Logger logger = Logger.getLogger(TimelineAction.class);
	
	@Resource
	private ITimelineApi timelineApi;
	
	@Autowired
	private StrategyDetailApi strategyDetailApi;
	
	@Autowired
	private  ProfileApi profileApi;
	
	private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static final String TIMELINE_CACHE_KEY="Timeline_";
	
	private static IMemcache<TimeLineBo> cache=MemCacheFactory.getLocalMemCache(TimeLineBo.class);
	
	/**
	 * 获取当前策略的所有基线
	 * @param profileId
	 * @return
	 * @throws ProfilelibException 
	 */
	@RequestMapping("/getTimelines")
	public JSONObject getTimelines(long profileId,String resourceId) throws ProfilelibException{
		//获取当前策略所有基线
		List<TimeLineBo> timeLines=timelineApi.getTimelinesByProfileId(profileId,resourceId);
		//缓存对象,超时半小时
		for(TimeLineBo timeLineBo:timeLines){
			cache.delete(TIMELINE_CACHE_KEY+timeLineBo.getTimeLineId());
			cache.set(TIMELINE_CACHE_KEY+timeLineBo.getTimeLineId(), timeLineBo,60*30);
		}
		
		List<TimelineVo> timelineVos=new ArrayList<TimelineVo>();
		
		for(TimeLineBo timelineBo:timeLines){
			TimelineVo timelineVo=new TimelineVo();
			
			BeanUtils.copyProperties(timelineBo, timelineVo);
			
			
			timelineVo.setStartTime(format.format(timelineBo.getStartTime()));
			timelineVo.setEndTime(format.format(timelineBo.getEndTime()));
			
			List<ProfileMetricBo> metrics=timelineBo.getMetricSetting().getMetrics();
			for(ProfileMetricBo profileMetricBo:metrics){
				String unit=profileMetricBo.getUnit();
				String metricName=profileMetricBo.getMetricName();
				if(!unit.isEmpty()){
					profileMetricBo.setMetricName(metricName+"("+unit+")");
				}
			}
			ProfileMetricPageVo profileMetricPageVo=new ProfileMetricPageVo();
			profileMetricPageVo.setTotalRecord(metrics.size());
			profileMetricPageVo.setRowCount(metrics.size());
			profileMetricPageVo.setMetrics(metrics);

			timelineVo.setProfileMetricPageVo(profileMetricPageVo);
			
			timelineVos.add(timelineVo);
		}

		
		return toSuccess(timelineVos);
		
	} 
	
	/**
	 * 获取当前策略的所有基线的数量
	 * @param profileId
	 * @return
	 * @throws ProfilelibException 
	 */
	@RequestMapping("/getTimelinesCount")
	public JSONObject getTimelinesCount(long profileId,String resourceId) throws ProfilelibException{
		//获取当前策略所有基线
		List<TimeLineBo> timeLines=timelineApi.getTimelinesByProfileId(profileId,resourceId);
		

		return toSuccess(timeLines!=null ? timeLines.size() : 0);
		
	}
	
	/**
	 * 获取指标
	 * @param profileId
	 * @return
	 * @throws ProfilelibException
	 */
	@RequestMapping("/getMetrics")
	public JSONObject getMetrics(long profileId) throws ProfilelibException{
		//获取策略的基本信息
		List<ProfileMetricBo> metrics=profileApi.getProfileMetrics(profileId);
		
		for(ProfileMetricBo profileMetricBo:metrics){
			String unit=profileMetricBo.getUnit();
			String metricName=profileMetricBo.getMetricName();
			if(!unit.isEmpty()){
				profileMetricBo.setMetricName(metricName+"("+unit+")");
			}
		}
		
		ProfileMetricPageVo profileMetricPageVo=new ProfileMetricPageVo();
		profileMetricPageVo.setTotalRecord(metrics.size());
		profileMetricPageVo.setRowCount(metrics.size());
		profileMetricPageVo.setMetrics(metrics);

		return toSuccess(profileMetricPageVo);
	}
	
	
	
	/**
	 * 根据基线ID 查询基线信息
	 * @param timelineId
	 * @return
	 * @throws ProfilelibException 
	 */
	@RequestMapping("/getTimeline")
	public JSONObject getTimeline(long timelineId,String resourceId) throws ProfilelibException{
		
		TimeLineBo timeLineBo=timelineApi.getTimeline(timelineId,resourceId);
		
		return toSuccess(timeLineBo);
	} 
	
	/**
	 * 添加基线
	 * @param profileId
	 * @param timelineVo
	 * @return
	 * @throws ProfilelibException 
	 */
	@RequestMapping("/addTimeline")
	public JSONObject addTimeline(TimelineVo timeline) throws ProfilelibException{
		
		TimeLineBo timelineBo=timelineVo2Bo(timeline);
	
		long timelineId=timelineApi.addTimeline(timelineBo);
		timelineBo.setTimeLineId(timelineId);
		timeline.setTimeLineId(timelineId);
		
		List<ProfileMetricBo> metrics=timelineBo.getMetricSetting().getMetrics();
		ProfileMetricPageVo profileMetricPageVo=new ProfileMetricPageVo();
		profileMetricPageVo.setTotalRecord(metrics.size());
		profileMetricPageVo.setRowCount(metrics.size());
		profileMetricPageVo.setMetrics(metrics);

		timeline.setProfileMetricPageVo(profileMetricPageVo);
		

		cache.set(TIMELINE_CACHE_KEY+timelineId, timelineBo,60*30);

		
		return toSuccess(timeline);
	}
	
	/**
	 * 更新基线
	 * @param timelineId
	 * @return
	 * @throws ProfilelibException 
	 */
	@RequestMapping("/updateTimeline")
	public JSONObject updateTimeline(TimelineVo timeline) throws ProfilelibException{
		
		TimeLineBo timelineBo=timelineVo2Bo(timeline);
		
		TimeLineBo oldTimeLineBo=cache.get(TIMELINE_CACHE_KEY+timelineBo.getTimeLineId());
		timelineApi.updateTimeline(oldTimeLineBo,timelineBo);
		
		TimelineVo timelineVo=new TimelineVo();
		
		BeanUtils.copyProperties(timelineBo, timelineVo);
		
		timelineVo.setStartTime(format.format(timelineBo.getStartTime()));
		timelineVo.setEndTime(format.format(timelineBo.getEndTime()));
		
		List<ProfileMetricBo> metrics=timelineBo.getMetricSetting().getMetrics();
		ProfileMetricPageVo profileMetricPageVo=new ProfileMetricPageVo();
		profileMetricPageVo.setTotalRecord(metrics.size());
		profileMetricPageVo.setRowCount(metrics.size());
		profileMetricPageVo.setMetrics(metrics);

		timelineVo.setProfileMetricPageVo(profileMetricPageVo);
		
		cache.set(TIMELINE_CACHE_KEY+timelineBo.getTimeLineId(), timelineBo,60*30);
		return toSuccess(timelineVo);
	}
	
	
	/**
	 * 删除基线
	 * @param timelineId
	 * @return
	 */
	@RequestMapping("/delTimeline")
	public JSONObject delTimeline(long timelineId){
		try {
			timelineApi.removeTimelineId(timelineId);
		} catch (ProfilelibException e) {
			logger.error("delTimeline Error:"+timelineId,e);
		}
		cache.delete(TIMELINE_CACHE_KEY+timelineId);
		return toSuccess(timelineId);
	}
	
	public TimeLineBo timelineVo2Bo(TimelineVo timeline){
		TimeLineBo timelineBo=new TimeLineBo();
		
		BeanUtils.copyProperties(timeline, timelineBo);

		JSONObject json=JSON.parseObject(timeline.getMetricsString());
//		String[] flappingArray=json.getString("flapping").split(",");
//		String[] alarmFlappingArray=json.getString("alarmFlapping").split(",");
		List<ProfileMetricBo> metricList=timeline.getBaseMetrics();

		if(metricList!=null && json != null && !json.isEmpty()){
			for(int i=0;i<metricList.size();i++){
				ProfileMetricBo profileMetricBo=metricList.get(i);
				String metricId=profileMetricBo.getMetricId();
				
//				profileMetricBo.setDictFrequencyId(flappingArray[i]);
//				profileMetricBo.setAlarmFlapping(Integer.parseInt(alarmFlappingArray[i]));
				
				if(json.getString(("monitor_"+metricId))!=null && json.getBoolean(("monitor_"+metricId))){
					profileMetricBo.setMonitor(true);
				}else{
					profileMetricBo.setMonitor(false);
				}
				
				if(json.getString(("alarm_"+metricId))!=null && json.getBoolean(("alarm_"+metricId))){
					profileMetricBo.setAlarm(true);
				}else{
					profileMetricBo.setAlarm(false);
				}
				
				
				if(json.getString(("dictFrequencyId_"+metricId))!=null ){
					profileMetricBo.setDictFrequencyId(json.getString(("dictFrequencyId_"+metricId)));
				}
				
				if(json.getString(("alarmFlapping_"+metricId))!=null ){
					profileMetricBo.setAlarmFlapping(json.getInteger(("alarmFlapping_"+metricId)));
				}
				
			}
		}
		
		MetricSettingBo metricSetting=new MetricSettingBo();
		metricSetting.setMetrics(metricList);
		timelineBo.setMetricSetting(metricSetting);
		
		
		return timelineBo;
	}
	
	
}
