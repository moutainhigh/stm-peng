package com.mainsteam.stm.portal.resource.api;

import java.util.List;

import com.mainsteam.stm.portal.resource.bo.TimeLineBo;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.Timeline;

public interface ITimelineApi {
	
	/**
	 * 获取基线 
	 * @param timelineId
	 * @return
	 */
	public TimeLineBo getTimeline(long timelineId,String resourceId) throws ProfilelibException ;
	
	/**
	 * 获取基线 
	 * @param profileId
	 * @return
	 */
	public List<TimeLineBo> getTimelinesByProfileId(long profileId,String resourceId) throws ProfilelibException;
	
	/**
	 * 添加基线
	 * @param profileId
	 * @param timeline
	 */
	public long addTimeline(TimeLineBo timeline) throws ProfilelibException;
	
	/**
	 * 添加基线
	 * @param profileId
	 * @param timelines
	 */
	public void addTimelineList(List<TimeLineBo> timelines) throws ProfilelibException;
	
	/**
	 * 更新基线
	 * @param profileId
	 * @param timelineOld 历史的基线
	 * @param timelineCurrent 当前更新的基线
	 */
	public void updateTimeline(TimeLineBo timelineOld,TimeLineBo timelineCurrent) throws ProfilelibException;
	
	/**
	 * 删除基线
	 * @param timelineId
	 */
	public void removeTimelineId(long timelineId) throws ProfilelibException;
}
