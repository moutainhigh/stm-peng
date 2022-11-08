package com.mainsteam.stm.profilelib;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.profilelib.obj.Threshold;
import com.mainsteam.stm.profilelib.obj.Timeline;
import com.mainsteam.stm.profilelib.obj.TimelineInfo;

public interface TimelineService {

	
	/**
	 * 查询所有的基线信息
	 * @param profileId 策略Id
	 * @return 基线信息
	 */
	public List<Timeline> getTimelines() throws ProfilelibException;
	
	/**
	 * 通过策略Id 查询所有的基线信息
	 * @param profileId 策略Id
	 * @return 基线信息
	 */
	public List<Timeline> getTimelinesByProfileId(long profileId) throws ProfilelibException;
	
	/**
	 * 通过策略Id 查询所有的基线基本信息
	 * @param profileId
	 * @return
	 * @throws ProfilelibException
	 */
	public List<TimelineInfo> getTimelineInfosByProfileId(long profileId) throws ProfilelibException;
	/**
	 * 通过基线Id 查询基线基本信息
	 * @param timelineId
	 * @return
	 * @throws ProfilelibException
	 */
	public TimelineInfo getTimelineInfosByTimelineId(long timelineId) throws ProfilelibException;
	
	/**
	 * 通过基线Id 获取基线信息
	 * @param timelineId
	 * @return 基线信息
	 * @throws ProfilelibException
	 */
	public Timeline getTimelinesById(long timelineId) throws ProfilelibException;
	
	/**
	 * 通过基线Id 查询所有的指标信息
	 * @param profileId 策略Id
	 * @return 指标信息
	 */
	public List<ProfileMetric> getMetricByTimelineId(long timelineId) throws ProfilelibException;
	/**
	 * 通过基线Id 查询指标信息
	 * @param profileId 策略Id
	 * @return 指标信息
	 */
	public ProfileMetric getMetricByTimelineIdAndMetricId(long timelineId,String metricId) throws ProfilelibException;
	
	public List<ProfileThreshold> getThresholdByTimelineIdAndMetricId(long timelineId,String metricId);
	/**
	 * 添加基线信息
	 * @param profileId
	 * @param timeline
	 */
	public long insertTimeline(Timeline timeline) throws ProfilelibException;
	/**
	 * 
	 * @param profileId
	 * @param timelines
	 */
	public void insertTimelines(List<Timeline> timelines) throws ProfilelibException;
	/**
	 * 移除基线通过基线id
	 * @param timelineId
	 */
	public void removeTimelineByTimelineId(long timelineId) throws ProfilelibException;
	
	/**
	 * 移除基线通过策略id
	 * @param profileId
	 */
	public void removeTimelineByProfileId(long profileId) throws ProfilelibException;
	
	/**
	 * 修改基线基本信息
	 * @param timelineInfo 基线基本信息
	 * @throws ProfilelibException
	 */
	public void updateTimelineInfo(TimelineInfo timelineInfo) throws ProfilelibException;
	
	/**
	 * 修改基线指标的监控频度 
	 * @param profileId
	 *            策略Id
	 * @param timelineId
	 *            基线Id
	 * @param frequencyValue
	 *            key：metricId指标Id value:频度值
	 * @throws ProfilelibException
	 */
	void updateTimelineMetricFrequency(long profileId,long timelineId,
			Map<String, String> frequencyValue) throws ProfilelibException;

	/**
	 * 修改基线的阈值 <br/>
	 * 业务场景：修改策略的阈值
	 * @param thresholds
	 *            key:metricId指标Id value:阈值
	 * @throws ProfilelibException
	 */
	void updateTimelineMetricThreshold(long timelineId,List<Threshold> thresholds) throws ProfilelibException;

	/**
	 * 修改基线的监控状态<br/>
	 * @param profileId
	 *            策略Id
	 * @param timelineId
	 *            基线Id
	 * @param monitor
	 *            key :metricId指标Id value:监控值
	 * @throws ProfilelibException
	 */
	void updateTimelineMetricMonitor(long profileId,long timelineId, Map<String, Boolean> monitor)
			throws ProfilelibException;

	/**
	 * 修改基线的告警状态<br/>
	 * 
	 * @param profileId
	 *            策略Id
	 * @param timelineId
	 *            基线Id
	 * @param alarms
	 *            key:metricId指标Id value:告警值
	 * @throws ProfilelibException
	 */
	void updateTimelineMetricAlarm(long profileId,long timelineId, Map<String, Boolean> alarms)
			throws ProfilelibException;
	
	/**
	 * 修改基线的flapping<br/>
	 * 
	 * @param profileId
	 *            策略Id
	 * @param timelineId
	 *            基线Id
	 * @param alarms
	 *            key:metricId指标Id value:flapping值
	 * @throws ProfilelibException
	 */
	void updateTimelineMetricflapping(long profileId,long timelineId, Map<String, Integer> flappings)
			throws ProfilelibException;

}
