package com.mainsteam.stm.profilelib.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.dict.PerfMetricStateEnum;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.profilelib.TimelineService;
import com.mainsteam.stm.profilelib.dao.ProfileChangeDAO;
import com.mainsteam.stm.profilelib.dao.ProfileMetricDAO;
import com.mainsteam.stm.profilelib.dao.ProfileThresholdDAO;
import com.mainsteam.stm.profilelib.dao.TimelineDAO;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.MetricSetting;
import com.mainsteam.stm.profilelib.obj.ProfileChangeData;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.profilelib.obj.Threshold;
import com.mainsteam.stm.profilelib.obj.Timeline;
import com.mainsteam.stm.profilelib.obj.TimelineInfo;
import com.mainsteam.stm.profilelib.objenum.ProfileChangeEnum;
import com.mainsteam.stm.profilelib.objenum.TimelineTypeEnum;
import com.mainsteam.stm.profilelib.po.ProfileChangePO;
import com.mainsteam.stm.profilelib.po.ProfileMetricPO;
import com.mainsteam.stm.profilelib.po.ProfileThresholdPO;
import com.mainsteam.stm.profilelib.po.TimelinePO;
import com.mainsteam.stm.profilelib.service.impl.querybean.QueryTimelineMetric;
import com.mainsteam.stm.profilelib.util.ProfileCache;

public class TimelineServiceImpl implements TimelineService {
	
	private static final Log logger = LogFactory.getLog(TimelineServiceImpl.class);
	
	private TimelineDAO timelineDAO;
	
	private ProfileMetricDAO profileMetricDAO;
	
	private ProfileThresholdDAO profileThresholdDAO;
	
	private ISequence ocTimelineSequence;
	
	private ISequence ocProfilelibMetricSequence;
	
	private ISequence ocProfilelibThresholdSequence;
	
	private ISequence ocProfileChangeSequence;
	
	private ProfileChangeDAO profileChangeDAO;
	
	private ProfileCache profileCache;
	
	public void setProfileCache(ProfileCache profileCache) {
		this.profileCache = profileCache;
	}

	public void setOcProfileChangeSequence(ISequence ocProfileChangeSequence) {
		this.ocProfileChangeSequence = ocProfileChangeSequence;
	}

	public void setProfileChangeDAO(ProfileChangeDAO profileChangeDAO) {
		this.profileChangeDAO = profileChangeDAO;
	}
	
	public void setOcProfilelibMetricSequence(ISequence ocProfilelibMetricSequence) {
		this.ocProfilelibMetricSequence = ocProfilelibMetricSequence;
	}

	public void setOcProfilelibThresholdSequence(
			ISequence ocProfilelibThresholdSequence) {
		this.ocProfilelibThresholdSequence = ocProfilelibThresholdSequence;
	}
	
	public void setOcTimelineSequence(ISequence ocTimelineSequence) {
		this.ocTimelineSequence = ocTimelineSequence;
	}

	public void setProfileThresholdDAO(ProfileThresholdDAO profileThresholdDAO) {
		this.profileThresholdDAO = profileThresholdDAO;
	}

	public void setProfileMetricDAO(ProfileMetricDAO profileMetricDAO) {
		this.profileMetricDAO = profileMetricDAO;
	}

	public void setTimelineDAO(TimelineDAO timelineDAO) {
		this.timelineDAO = timelineDAO;
	}
	
	private ProfileMetricPO tranProfileMetrics(long profileId,long timelineId,ProfileMetric profileMetric){
		ProfileMetricPO profileMetricPO = new ProfileMetricPO();
		profileMetricPO.setDictFrequencyId(profileMetric.getDictFrequencyId());
		profileMetricPO.setIsAlarm(profileMetric.isAlarm()?"1":"0");
		profileMetricPO.setIsUse(profileMetric.isMonitor()?"1":"0");
		profileMetricPO.setMetricId(profileMetric.getMetricId());
		profileMetricPO.setProfileId(profileId);
		profileMetricPO.setTimelineId(timelineId);
		if(profileMetric.getMetric_mkId() != null){
			profileMetricPO.setMkId(Long.parseLong(profileMetric.getMetric_mkId()));
		}
		profileMetricPO.setAlarmRepeat(profileMetric.getAlarmFlapping());
		return profileMetricPO;
	}
	
	private ProfileThresholdPO tranProfileThreshold(long profileId,long timelineId,ProfileThreshold profileThreshold){
		ProfileThresholdPO thresholdPO = new ProfileThresholdPO();
		thresholdPO.setMkId(profileThreshold.getThreshold_mkId());
		thresholdPO.setMetricId(profileThreshold.getMetricId());
		thresholdPO.setDictMetricState(profileThreshold.getPerfMetricStateEnum().toString());
		thresholdPO.setExpressionDesc(profileThreshold.getThresholdExpression());
		thresholdPO.setExpressionOperator(profileThreshold.getExpressionOperator());
		thresholdPO.setThresholdValue(profileThreshold.getThresholdValue());
		thresholdPO.setAlarmTemplate(StringUtils.trimToNull(profileThreshold.getAlarmTemplate()));
		thresholdPO.setProfileId(profileId);
		thresholdPO.setTimelineId(timelineId);
		return thresholdPO;
	}
	
	private List<ProfileMetric> tranProfileMetricPO2BOs(
			List<ProfileMetricPO> metricsPOs) {
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
	
	private TimelinePO tranTimelinePO2BO(TimelineInfo timeline){
		TimelinePO timelinePO = new TimelinePO();
		timelinePO.setEndTime(timeline.getEndTime());
		timelinePO.setStartTime(timeline.getStartTime());
		timelinePO.setLineName(timeline.getName());
		timelinePO.setLineType(timeline.getTimeLineType().toString());
		timelinePO.setProfileId(timeline.getProfileId());
		return timelinePO;
	}
	
	private TimelineInfo tranTimelinePO(TimelinePO timelinePO){
		TimelineInfo timelineInfo = new TimelineInfo();
		timelineInfo.setEndTime(timelinePO.getEndTime());
		timelineInfo.setStartTime(timelinePO.getStartTime());
		timelineInfo.setTimeLineId(timelinePO.getLineId());
		timelineInfo.setTimeLineType(TimelineTypeEnum.valueOf(timelinePO.getLineType()));
		timelineInfo.setName(timelinePO.getLineName());
		timelineInfo.setTimeLineId(timelinePO.getLineId());
		timelineInfo.setProfileId(timelinePO.getProfileId());
		return timelineInfo;
	}
	
	private List<ProfileThreshold> transProfileThresholdPO2BOs(
			List<ProfileThresholdPO> thresholdPOs) {
		List<ProfileThreshold> thresholds = new ArrayList<ProfileThreshold>();

		for (ProfileThresholdPO thresholdPO : thresholdPOs) {
			ProfileThreshold threshold = transProfileThresholdPO2BO(thresholdPO);
			thresholds.add(threshold);
		}
		return thresholds;
	}

	@SuppressWarnings("deprecation")
	private ProfileThreshold transProfileThresholdPO2BO(ProfileThresholdPO po) {
		ProfileThreshold bo = new ProfileThreshold();
		String stateStr = po.getDictMetricState();
		PerfMetricStateEnum stateEnum = PerfMetricStateEnum.valueOf(stateStr);
		bo.setPerfMetricStateEnum(stateEnum);
		bo.setThresholdExpression(po.getExpressionDesc());
		bo.setExpressionOperator(po.getExpressionOperator());
		bo.setThresholdValue(po.getThresholdValue());
		bo.setMetricId(po.getMetricId());
		bo.setThreshold_mkId(po.getMkId());
		bo.setProfileId(po.getProfileId());
		bo.setTimelineId(po.getTimelineId());
		bo.setAlarmTemplate(StringUtils.trimToNull(po.getAlarmTemplate()));
		return bo;
	}
	
	private QueryTimelineMetric convertToQyeryProfileMetric(ProfileMetric metric){
		return new QueryTimelineMetric(metric, this);
	}
	
	@Override
	public List<Timeline> getTimelines() throws ProfilelibException {
		if(logger.isTraceEnabled()){
			logger.trace("getTimelines start");
		}
		List<Timeline> timelines = new ArrayList<>();
		try {
			List<TimelinePO> timelinePOs = timelineDAO.getAllTimeline();
			if(timelinePOs !=null && !timelinePOs.isEmpty()){
				for (TimelinePO timelinePO : timelinePOs) {
					timelines.add(queryTimeline(timelinePO));
				}
			}
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
		}
		if(logger.isTraceEnabled()){
			logger.trace("getTimelines end");
		}
		return timelines;
	}
	
	@Override
	public List<Timeline> getTimelinesByProfileId(long profileId) {
		if(logger.isTraceEnabled()){
			logger.trace("getTimelinesByProfileId start profileId=" + profileId);
		}
		List<Timeline> timelines = new ArrayList<>();
		try {
			List<TimelinePO> timelinePOs = timelineDAO.getTimelineByProfileId(profileId);
			if(timelinePOs !=null && !timelinePOs.isEmpty()){
				for (TimelinePO timelinePO : timelinePOs) {
					timelines.add(queryTimeline(timelinePO));
				}
			}
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
		}
		if(logger.isTraceEnabled()){
			logger.trace("getTimelinesByProfileId end profileId=" + profileId);
		}
		return timelines;
	}

	
	public List<TimelineInfo> getTimelineInfosByProfileId(long profileId) throws ProfilelibException {
		List<TimelineInfo> timelines = null;
		try {
			List<TimelinePO> timelinePOs = timelineDAO.getTimelineByProfileId(profileId);
			timelines = new ArrayList<TimelineInfo>(timelinePOs.size());
			if(timelinePOs !=null && !timelinePOs.isEmpty()){
				for (TimelinePO timelinePO : timelinePOs) {
					timelines.add(tranTimelinePO(timelinePO));
				}
			}
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
		}
		return timelines;
	}
	
	@Override
	public TimelineInfo getTimelineInfosByTimelineId(long timelineId)
			throws ProfilelibException {
		TimelineInfo timelineInfo = null;
		try {
			TimelinePO timelinePO = timelineDAO.getTimelineById(timelineId);
			if(timelinePO !=null){
				timelineInfo = tranTimelinePO(timelinePO);
			}
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
		}
		return timelineInfo;
	}

	
	@Override
	public Timeline getTimelinesById(long timelineId)
			throws ProfilelibException {
		if(logger.isTraceEnabled()){
			logger.trace("getTimelinesById start timelineId=" + timelineId);
		}
		Timeline timeline = null;
		try {
			TimelinePO timelinePO = timelineDAO.getTimelineById(timelineId);
			if(timelinePO != null){
				timeline = queryTimeline(timelinePO);
			}
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
		}
		if(logger.isTraceEnabled()){
			logger.trace("getTimelinesById end timelineId=" + timelineId);
		}
		return timeline;
	}
	
	@Override
	public ProfileMetric getMetricByTimelineIdAndMetricId(long timelineId,
			String metricId) throws ProfilelibException {
		if(logger.isTraceEnabled()){
			logger.trace("getMetricByTimelineIdAndMetricId start timelineId=" + timelineId + "metricId=" + metricId);
		}
		//metric
		ProfileMetric result = profileCache.getTimelineMetricBymetricId(timelineId, metricId);
		if(result != null){
			//使用懒加载阈值
			return convertToQyeryProfileMetric(result);
//			List<ProfileThreshold> thresholds = getThresholdByTimelineIdAndMetricId(timelineId, metricId);
//			if(thresholds != null){
//				result.setMetricThresholds(thresholds);
//			}
//			if(logger.isTraceEnabled()){
//				logger.trace("getMetricByTimelineIdAndMetricId end timelineId" + timelineId + "metricId=" + metricId);
//			}
//			return result;
		}
		List<ProfileMetric> metrics = null;
		try {
			List<ProfileMetricPO> profileMetricPOs = profileMetricDAO.getMetricsByTimelineId(timelineId);
			metrics = tranProfileMetricPO2BOs(profileMetricPOs);
		
			Map<String, ProfileMetric> metricsMap = new HashMap<>();
			for (ProfileMetric profileMetric : metrics) {
				if(metricId.equals(profileMetric.getMetricId())){
					metricsMap.put(profileMetric.getMetricId(), profileMetric);
					break;
				}
			}
			//threshold
			List<ProfileThresholdPO> thresholdPOs = profileThresholdDAO.getThresholdByTimelineId(timelineId);
			List<ProfileThreshold> thresholds = transProfileThresholdPO2BOs(thresholdPOs);
			for (ProfileThreshold profileThreshold : thresholds) {
				if (metricsMap.containsKey(profileThreshold.getMetricId())) {
					ProfileMetric m = metricsMap.get(profileThreshold.getMetricId());
					if (m.getMetricThresholds() == null) {
						m.setMetricThresholds(new ArrayList<ProfileThreshold>());
					}
					m.getMetricThresholds().add(profileThreshold);
				}
			}
			//add to cache
			for (Entry<String,ProfileMetric> item : metricsMap.entrySet()) {
				profileCache.addTimelineMetric(item.getValue());
				List<ProfileThreshold> profileThresholds = item.getValue().getMetricThresholds();
				if(profileThresholds != null && !profileThresholds.isEmpty()){
					profileCache.addTimelineThreshold(timelineId, item.getKey(), thresholds);
				}
			}
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("getMetricByTimelineId", e);
			}
		}
		if(logger.isTraceEnabled()){
			logger.trace("getMetricByTimelineIdAndMetricId end timelineId=" + timelineId + "metricId=" + metricId);
		}
		return result;
	}
	
	@Override
	public List<ProfileThreshold> getThresholdByTimelineIdAndMetricId(
			long timelineId, String metricId) {
		if(logger.isTraceEnabled()){
			logger.trace("getThresholdByTimelineIdAndMetricId start timelineId=" + timelineId + "metricId=" + metricId);
		}
		//threshold
		List<ProfileThreshold> thresholds = null;
		List<ProfileThreshold> result = null;
		try {
			thresholds = profileCache.getTimelineThresholdBymetricId(timelineId,metricId);
			if(thresholds != null){
				return thresholds;
			}
			List<ProfileThresholdPO> thresholdPOs = profileThresholdDAO.getThresholdByTimelineId(timelineId);
			result = new ArrayList<ProfileThreshold>();
			for (ProfileThresholdPO profileThresholdPO : thresholdPOs) {
				if (metricId.equals(profileThresholdPO.getMetricId())) {
					ProfileThreshold profileThreshold = transProfileThresholdPO2BO(profileThresholdPO);
					result.add(profileThreshold);
				}
			}
//			if(result.isEmpty()){
//				//add to cache
//				profileCache.addTimelineThreshold(timelineId, metricId, result);
//			}else{
				//add to cache 如果没有值，默认放一个空集合
				profileCache.addTimelineThreshold(timelineId, metricId, result);
//			}
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
		}
		if(logger.isTraceEnabled()){
			logger.trace("getThresholdByTimelineIdAndMetricId end timelineId=" + timelineId + "metricId=" + metricId);
		}
		return thresholds;
	}
	@Override
	public List<ProfileMetric> getMetricByTimelineId(long timelineId) throws ProfilelibException {
		if(logger.isTraceEnabled()){
			logger.trace("getMetricByTimelineId start timelineId=" + timelineId);
		}
		//先取缓存
		List<ProfileMetric> metrics = null;
		HashSet<String> metricIds = profileCache.getMetricIdByTimelineId(timelineId);
		if(metricIds != null){
			metrics = new ArrayList<ProfileMetric>(20);
			for (String metricId : metricIds) {
				ProfileMetric profileMetric = getMetricByTimelineIdAndMetricId(timelineId, metricId);
				if(profileMetric != null){
//					List<ProfileThreshold> profileThresholds = getThresholdByTimelineIdAndMetricId(timelineId, metricId);
//					if(profileThresholds != null){
//						profileMetric.setMetricThresholds(profileThresholds);
//					}
					metrics.add(convertToQyeryProfileMetric(profileMetric));
				}
			}
			if(logger.isTraceEnabled()){
				logger.trace("cache getMetricByTimelineId end timelineId=" + timelineId);
			}
			return metrics;
		}
		try {
			List<ProfileMetric> cacheMetrics = new ArrayList<ProfileMetric>();
			List<ProfileMetricPO> profileMetricPOs = profileMetricDAO.getMetricsByTimelineId(timelineId);
			Map<String, ProfileMetric> metricsMap = new HashMap<>();
			Map<Long,HashSet<String>> timelineMetricIds = new HashMap<Long, HashSet<String>>();
			for (ProfileMetricPO profileMetricPO : profileMetricPOs) {
				ProfileMetric profileMetric = tranProfileMetricPO2BO(profileMetricPO);
				metricsMap.put(profileMetric.getMetricId(), profileMetric);
				cacheMetrics.add(profileMetric);
				
				HashSet<String> ids = timelineMetricIds.get(profileMetricPO.getTimelineId());
				if(ids == null){
					ids = new HashSet<String>(20);
					timelineMetricIds.put(profileMetricPO.getTimelineId(), ids);
				}
				ids.add(profileMetricPO.getMetricId());
			}
			//threshold
			List<ProfileThresholdPO> thresholdPOs = profileThresholdDAO.getThresholdByTimelineId(timelineId);
			List<ProfileThreshold> thresholds = transProfileThresholdPO2BOs(thresholdPOs);
			for (ProfileThreshold profileThreshold : thresholds) {
				if (metricsMap.containsKey(profileThreshold.getMetricId())) {
					ProfileMetric m = metricsMap.get(profileThreshold.getMetricId());
					if (m.getMetricThresholds() == null) {
						m.setMetricThresholds(new ArrayList<ProfileThreshold>());
					}
					m.getMetricThresholds().add(profileThreshold);
				}
			}
			//add to cache
			for (ProfileMetric profileMetric : cacheMetrics) {
				profileCache.addTimelineMetric(profileMetric);
				ProfileMetric m =  metricsMap.get(profileMetric.getMetricId());
				if(m != null){
					List<ProfileThreshold> profileThresholds = m.getMetricThresholds();
					if(profileThresholds != null && !profileThresholds.isEmpty()){
						profileCache.addTimelineThreshold(timelineId,profileMetric.getMetricId(), profileThresholds);
					}
				}
			}
			if(!timelineMetricIds.isEmpty()){
				for (Entry<Long,HashSet<String>> item : timelineMetricIds.entrySet()) {
					profileCache.setTimelineMetricKey(item.getKey(), item.getValue());
				}
			}
			metrics = new ArrayList<ProfileMetric>(metricsMap.values());
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("getMetricByTimelineId", e);
			}
		}
		if(logger.isTraceEnabled()){
			logger.trace("getTimelinesByProfileId end timelineId="+ timelineId);
		}
		return metrics;
	}

	@Override
	public long insertTimeline(Timeline timeline) throws ProfilelibException{
		if(logger.isTraceEnabled()){
			logger.trace("insertTimeline start profileId=" + timeline.getTimelineInfo().getProfileId() +" timeline name="+timeline.getTimelineInfo().getName());
		}
		long timelineId = ocTimelineSequence.next();
		TimelineInfo timelineInfo = timeline.getTimelineInfo();
		TimelinePO timelinePO =  tranTimelinePO2BO(timelineInfo);
		timelinePO.setLineId(timelineId);
		try {
			List<ProfileMetric> profileMetrics = timeline.getMetricSetting().getMetrics();
			//查找基线指标值
			List<ProfileMetricPO> profileMetricPOs = new ArrayList<ProfileMetricPO>(20); 
			//查找基线阈值
			List<ProfileThresholdPO> profileThresholdPOs = new ArrayList<>();
			for(ProfileMetric profileMetric : profileMetrics){
				long mk_id = ocProfilelibMetricSequence.next();
				profileMetric.setMetric_mkId(String.valueOf(mk_id));
				profileMetricPOs.add(tranProfileMetrics(timeline.getTimelineInfo().getProfileId(), timelineId, profileMetric));
				if(profileMetric.getMetricThresholds() != null){
					for (ProfileThreshold profileThreshold : profileMetric.getMetricThresholds()) {
						profileThreshold.setThreshold_mkId(ocProfilelibThresholdSequence.next());
						profileThresholdPOs.add(tranProfileThreshold(timelineInfo.getProfileId(),timelineId,profileThreshold));
					}
				}
			}
			timelineDAO.insertTimeline(timelinePO);
			//插入指标跟基线关系
			profileMetricDAO.insertMetrics(profileMetricPOs);
			if(!profileThresholdPOs.isEmpty()){
				//修改指标阈值跟基线关系
				profileThresholdDAO.insertThresholds(profileThresholdPOs);
			}
			ProfileChangePO profileChangePO = new ProfileChangePO();
			profileChangePO.setChangeTime(new Date());
			profileChangePO.setOperateMode(ProfileChangeEnum.ADD_TIMELINE.toString());
			profileChangePO.setOperateState(0);
			profileChangePO.setProfileId(timelineInfo.getProfileId());
			profileChangePO.setSource(String.valueOf(timelineId));
			profileChangePO.setProfileChangeId(ocProfileChangeSequence.next());
			profileChangeDAO.insertProfileChange(profileChangePO);
			//add to cache
			for(ProfileMetric profileMetric : profileMetrics){
				profileCache.addTimelineMetric(profileMetric);
				List<ProfileThreshold> thresholds = profileMetric.getMetricThresholds();
				if(thresholds != null && !thresholds.isEmpty()){
					profileCache.addTimelineThreshold(timelineId, profileMetric.getMetricId(), thresholds);
				}
			}
		} catch (Exception e1) {
			if(logger.isErrorEnabled()){
				logger.error("", e1);
			}
			throw new ProfilelibException(423, "");
		}
		if(logger.isTraceEnabled()){
			logger.trace("insertTimeline end");
		}
		return timelineId;
	}

	@Override
	public void insertTimelines(List<Timeline> timelines) throws ProfilelibException{
		if(logger.isTraceEnabled()) {
			logger.trace("insertTimelines start");
		}
		// TODO Auto-generated catch block
		for(Timeline timeline : timelines){	
			insertTimeline(timeline);	
		}
		if(logger.isTraceEnabled()){
			logger.trace("insertTimelines end");
		}
	}

	@Override
	public void removeTimelineByTimelineId(long timelineId) throws ProfilelibException{
		if(logger.isTraceEnabled()){
			logger.trace("removeTimelineByTimelineId start timelineId="+timelineId);
		}
		try {
			TimelinePO po = timelineDAO.getTimelineById(timelineId);
			//删除基线基本信息
			timelineDAO.removeTimelineById(timelineId);
			//删除基线阈值
			profileThresholdDAO.removeThresholdByTimelineId(timelineId);
			//删除基线指标
			profileMetricDAO.removeMetricByTimelineId(timelineId);
			ProfileChangePO profileChangePO = new ProfileChangePO();
			profileChangePO.setChangeTime(new Date());
			profileChangePO.setOperateMode(ProfileChangeEnum.DELETE_TIMELINE.toString());
			profileChangePO.setOperateState(0);
			profileChangePO.setProfileId(po.getProfileId());
			profileChangePO.setSource(String.valueOf(timelineId));
			profileChangePO.setProfileChangeId(ocProfileChangeSequence.next());
			profileChangeDAO.insertProfileChange(profileChangePO);
			//remove cache
			deleteTimelineCacheByTimelineId(timelineId);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
			throw new ProfilelibException(54,"");
		}
		
		if(logger.isTraceEnabled()){
			logger.trace("removeTimelineByTimelineId end");
		}
	}
	
	private void deleteTimelineCacheByTimelineId(long timelineId){
		HashSet<String> metricIds = profileCache.getMetricIdByTimelineId(timelineId);
		if(metricIds != null){
			for (String metricId : metricIds) {
				profileCache.deleteTimelineMetric(timelineId, metricId);
				profileCache.deleteTimelineThreshold(timelineId, metricId);
			}
		}
	}
	/**
	 * 移除基线通过策略id
	 * @param profileId
	 */
	public void removeTimelineByProfileId(long profileId) throws ProfilelibException{
		if(logger.isTraceEnabled()){
			logger.trace("removeTimelineByTimelineId start profileId="+profileId);
		}
		try {
			List<TimelinePO> timelinePOs = timelineDAO.getTimelineByProfileId(profileId);
			List<Long> timelineIds = null;
			if(timelinePOs != null && !timelinePOs.isEmpty()){
				timelineIds = new ArrayList<Long>();
				for (TimelinePO timelinePO : timelinePOs) {
					timelineIds.add(timelinePO.getLineId());
				}
			}
			//删除基线基本信息
			timelineDAO.removeTimelineByProfileId(profileId);	
			if(timelineIds != null){
				profileThresholdDAO.removeThresholdByTimelineIds(timelineIds);
				profileMetricDAO.removeMetricByTimelineIds(timelineIds);
				List<ProfileChangePO> profileChangePOs = new ArrayList<>();
				Date date = new Date();
				for (long timelineId : timelineIds) {
					ProfileChangePO profileChangePO = new ProfileChangePO();
					profileChangePO.setChangeTime(date);
					profileChangePO.setOperateMode(ProfileChangeEnum.DELETE_TIMELINE.toString());
					profileChangePO.setOperateState(0);
					profileChangePO.setProfileId(profileId);
					profileChangePO.setSource(String.valueOf(timelineId));
					profileChangePO.setProfileChangeId(ocProfileChangeSequence.next());
					profileChangePOs.add(profileChangePO);
				}
				profileChangeDAO.insertProfileChanges(profileChangePOs);
				//remove timeline cache
				for (Long timelineId : timelineIds) {
					deleteTimelineCacheByTimelineId(timelineId);
				}
			}
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
			throw new ProfilelibException(54,"");
		}
		if(logger.isTraceEnabled()){
			logger.trace("removeTimelineByTimelineId end");
		}
	}

	@Override
	public void updateTimelineInfo(TimelineInfo timelineInfo) throws ProfilelibException{
		if(logger.isTraceEnabled()){
			logger.trace("updateTimelineInfo start");
		}
		long timelineId = timelineInfo.getTimeLineId();
		TimelinePO timelinePO =  tranTimelinePO2BO(timelineInfo);
		timelinePO.setLineId(timelineId);
		try {
			timelineDAO.updateTimeline(timelinePO);
			Date date = new Date();
			ProfileChangePO profileChangePO = new ProfileChangePO();
			profileChangePO.setChangeTime(date);
			profileChangePO.setOperateMode(ProfileChangeEnum.UPDATE_TIMELINE_TIME.toString());
			profileChangePO.setOperateState(0);
			profileChangePO.setProfileId(timelineInfo.getProfileId());
			profileChangePO.setSource(String.valueOf(timelineId));
			profileChangePO.setTimelineId(timelineId);
			profileChangePO.setProfileChangeId(ocProfileChangeSequence.next());
			profileChangeDAO.insertProfileChange(profileChangePO);
		} catch (Exception e1) {
			if(logger.isErrorEnabled()){
				logger.error("", e1);
			}
			throw new ProfilelibException(32,"");
		}
		if(logger.isTraceEnabled()){
			logger.trace("updateTimelineInfo end");
		}

	}
	
	
	private Timeline queryTimeline(TimelinePO timelinePO) throws Exception{
		Timeline timeline = new Timeline();
		TimelineInfo timelineInfo = tranTimelinePO(timelinePO);
		MetricSetting metricSetting = new MetricSetting();
		timeline.setMetricSetting(metricSetting);
		timeline.setTimelineInfo(timelineInfo);
		List<ProfileMetric> profileMetrics = getMetricByTimelineId(timelineInfo.getTimeLineId());
		metricSetting.setMetrics(profileMetrics);
		return timeline;
	}

	@Override
	public void updateTimelineMetricFrequency(long profileId,long timelineId,
			Map<String, String> frequencyValue) throws ProfilelibException {
		if(logger.isTraceEnabled()){
			logger.trace("updateTimelineMetricFrequency start");
		}
		List<ProfileMetricPO> updateProfileMetricPOs = new ArrayList<ProfileMetricPO>();
		List<ProfileChangePO> profileChangePOs = new ArrayList<>();
		Date date = new Date();
		List<ProfileMetric> profileMetrics = new ArrayList<ProfileMetric>(frequencyValue.size());
		for (Entry<String, String> item : frequencyValue.entrySet()) {
			ProfileMetricPO profileMetricPO = new ProfileMetricPO();
			profileMetricPO.setDictFrequencyId(item.getValue());
			profileMetricPO.setMetricId(item.getKey());
			profileMetricPO.setProfileId(profileId);
			profileMetricPO.setTimelineId(timelineId);
			updateProfileMetricPOs.add(profileMetricPO);

			ProfileChangePO profileChangePO = new ProfileChangePO();
			long profileChangeId = ocProfileChangeSequence.next();
			profileChangePO.setProfileChangeId(profileChangeId);
			profileChangePO.setOperateState(0);
			profileChangePO.setProfileId(profileId);
			profileChangePO.setTimelineId(timelineId);
			profileChangePO.setChangeTime(date);
			profileChangePO
					.setOperateMode(ProfileChangeEnum.UPDATE_TIMELINE_MONITORFEQ
							.toString());
			profileChangePO.setSource(item.getKey());
			profileChangePOs.add(profileChangePO);
			ProfileMetric metric = profileCache.getTimelineMetricBymetricId(timelineId, item.getKey());
			if(metric != null){
				profileMetrics.add(metric);
			}
		}
		try {
			profileMetricDAO.updateProfileMetrics(updateProfileMetricPOs);
			// 更新到采集器到数据库
			profileChangeDAO.insertProfileChanges(profileChangePOs);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateProfileMetrics error", e);
			}
			throw new ProfilelibException(23,
					"updateProfileMetricFrequency error");
		}
		//更新缓存
		for (ProfileMetric profileMetric : profileMetrics) {
			String value = frequencyValue.get(profileMetric.getMetricId());
			profileMetric.setDictFrequencyId(value);
			profileCache.updateTimelineMetric(profileMetric);
		}
		if(logger.isTraceEnabled()){
			logger.trace("updateTimelineMetricFrequency end");
		}
		
	}

	@Override
	public void updateTimelineMetricThreshold(long timelineId,List<Threshold> thresholds)
			throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateTimelineMetricThreshold start");
		}
		List<ProfileThresholdPO> updateProfileThresholdPOs = new ArrayList<>();
		Map<Long,Threshold> updateThreholds = new HashMap<>();
		BigDecimal bigdecimal = null;
		for (Threshold threshold : thresholds) {
			ProfileThresholdPO profileThresholdPO = new ProfileThresholdPO();
			//过滤科学计算法.用正常的数字显示
			if(StringUtils.isNotEmpty(threshold.getThresholdValue())){
				bigdecimal = new BigDecimal(threshold.getThresholdValue());
				String thresholdValue = bigdecimal.toPlainString();
				threshold.setThresholdValue(thresholdValue);
				profileThresholdPO.setThresholdValue(thresholdValue);
			}
			profileThresholdPO.setExpressionDesc(threshold.getThresholdExpression());
			if(StringUtils.isNotEmpty(threshold.getExpressionOperator())){
				profileThresholdPO.setExpressionOperator(threshold.getExpressionOperator());
			}
			profileThresholdPO.setAlarmTemplate(StringUtils.trimToEmpty(threshold.getAlarmTemplate()));
			profileThresholdPO.setMkId(threshold.getThreshold_mkId());
			updateProfileThresholdPOs.add(profileThresholdPO);
			updateThreholds.put(threshold.getThreshold_mkId(), threshold);
		}
		try {
			profileThresholdDAO.updateThresholds(updateProfileThresholdPOs);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateTimelineMetricThreshold error", e);
			}
			throw new ProfilelibException(121,"");
		}
		//修改缓存
		HashSet<String> metricIds = profileCache.getMetricIdByTimelineId(timelineId);
		if(metricIds != null){
			//key: metricId
			for (String metricId : metricIds) {
				List<ProfileThreshold> profileThresholds = profileCache.getTimelineThresholdBymetricId(timelineId, metricId);
				boolean isUpdate = false;
				if(profileThresholds != null){
					for(ProfileThreshold profileThreshold : profileThresholds){
						Threshold threshold = updateThreholds.get(profileThreshold.getThreshold_mkId());
						if(threshold != null){ 
							profileThreshold.setThresholdValue(threshold.getThresholdValue());
							profileThreshold.setAlarmTemplate(StringUtils.trimToNull(threshold.getAlarmTemplate()));
							profileThreshold.setThresholdExpression(StringUtils.trimToNull(threshold.getThresholdExpression()));
							isUpdate = true;
						}
					}
				}
				if(isUpdate){
					profileCache.updateTimelineThreshold(timelineId, metricId, profileThresholds);
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("updateTimelineMetricThreshold end");
		}
	}

	@Override
	public void updateTimelineMetricMonitor(long profileId,long timelineId,
			Map<String, Boolean> monitor) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateTimelineMetricMonitor start");
		}
		List<ProfileChangeData> profileMetricChangeDatas = new ArrayList<ProfileChangeData>(monitor.size());
		List<ProfileMetricPO> updateProfileMetricPOs = new ArrayList<ProfileMetricPO>();
		List<ProfileChangePO> profileChangePOs = new ArrayList<>();
		List<ProfileMetric> profileMetrics = new ArrayList<ProfileMetric>(monitor.size());
		Date date = new Date();
		for (Entry<String, Boolean> item : monitor.entrySet()) {
			ProfileMetricPO profileMetricPO = new ProfileMetricPO();
			profileMetricPO.setIsUse(item.getValue() == true ? "1" : "0");
			profileMetricPO.setMetricId(item.getKey());
			profileMetricPO.setProfileId(profileId);
			profileMetricPO.setTimelineId(timelineId);
			updateProfileMetricPOs.add(profileMetricPO);

			ProfileChangePO profileChangePO = new ProfileChangePO();
			long profileChangeId = ocProfileChangeSequence.next();
			profileChangePO.setProfileChangeId(profileChangeId);
			profileChangePO.setProfileId(profileId);
			profileChangePO.setTimelineId(timelineId);
			profileChangePO.setOperateState(0);
			profileChangePO.setChangeTime(date);
			profileChangePO
					.setOperateMode(ProfileChangeEnum.UPDATE_TIMELINE_MONITOR
							.toString());
			profileChangePO.setSource(item.getKey());
			profileChangePOs.add(profileChangePO);
			
			ProfileChangeData profileMetricChangeData = new ProfileChangeData();
			profileMetricChangeData.setIsMonitor(item.getValue());
			profileMetricChangeData.setProfileId(profileId);
			profileMetricChangeData.setMetricId(item.getKey());
			profileMetricChangeDatas.add(profileMetricChangeData);
			ProfileMetric metric = profileCache.getTimelineMetricBymetricId(timelineId, item.getKey());
			if(metric != null){
				profileMetrics.add(metric);
			}
		}
		try {
			profileMetricDAO.updateProfileMetrics(updateProfileMetricPOs);
			profileChangeDAO.insertProfileChanges(profileChangePOs);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateTimelineMetricMonitor error", e);
			}
			throw new ProfilelibException(32,"");
		}
		//更新缓存
		for (ProfileMetric profileMetric : profileMetrics) {
			boolean isMontior = monitor.get(profileMetric.getMetricId());
			profileMetric.setMonitor(isMontior);
			profileCache.updateTimelineMetric(profileMetric);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("updateTimelineMetricMonitor end");
		}
		
	}

	@Override
	public void updateTimelineMetricAlarm(long profileId,long timelineId,
			Map<String, Boolean> alarms) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateTimelineMetricAlarm start");
		}
		List<ProfileMetricPO> updateProfileMetricPOs = new ArrayList<ProfileMetricPO>(alarms.size());
		List<ProfileMetric> profileMetrics = new ArrayList<ProfileMetric>(alarms.size());
		for (Entry<String, Boolean> item : alarms.entrySet()) {
			ProfileMetricPO profileMetricPO = new ProfileMetricPO();
			profileMetricPO.setIsAlarm(item.getValue() == true ? "1" : "0");
			profileMetricPO.setMetricId(item.getKey());
			profileMetricPO.setProfileId(profileId);
			profileMetricPO.setTimelineId(timelineId);
			updateProfileMetricPOs.add(profileMetricPO);
			ProfileMetric metric = profileCache.getTimelineMetricBymetricId(timelineId, item.getKey());
			if(metric != null){
				profileMetrics.add(metric);
			}
		}
		try {
			profileMetricDAO.updateProfileMetrics(updateProfileMetricPOs);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateTimelineMetricAlarm error", e);
			}
			throw new ProfilelibException(423, "");
		}
		for (ProfileMetric profileMetric : profileMetrics) {
			boolean isAlarm = alarms.get(profileMetric.getMetricId());
			profileMetric.setAlarm(isAlarm);
			profileCache.updateTimelineMetric(profileMetric);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("updateTimelineMetricAlarm end");
		}
	}

	@Override
	public void updateTimelineMetricflapping(long profileId,long timelineId,
			Map<String, Integer> flappings) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateTimelineMetricflapping start");
		}
		List<ProfileMetricPO> updateProfileMetricPOs = new ArrayList<ProfileMetricPO>();
		List<ProfileMetric> profileMetrics = new ArrayList<ProfileMetric>(flappings.size());
		for (Entry<String, Integer> item : flappings.entrySet()) {
			ProfileMetricPO profileMetricPO = new ProfileMetricPO();
			profileMetricPO.setAlarmRepeat(item.getValue());
			profileMetricPO.setMetricId(item.getKey());
			profileMetricPO.setProfileId(profileId);
			profileMetricPO.setTimelineId(timelineId);
			updateProfileMetricPOs.add(profileMetricPO);
			ProfileMetric metric = profileCache.getTimelineMetricBymetricId(timelineId, item.getKey());
			if(metric != null){
				profileMetrics.add(metric);
			}
		}
		try {
			profileMetricDAO.updateProfileMetrics(updateProfileMetricPOs);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateTimelineMetricflapping error", e);
			}
			throw new ProfilelibException(243, "");
		}
		//更新缓存
		for (ProfileMetric profileMetric : profileMetrics) {
			int flapping = flappings.get(profileMetric.getMetricId());
			profileMetric.setAlarmFlapping(flapping);
			profileCache.updateTimelineMetric(profileMetric);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("updateTimelineMetricflapping end");
		}
	}

	public TimelineDAO getTimelineDAO() {
		return timelineDAO;
	}
}
