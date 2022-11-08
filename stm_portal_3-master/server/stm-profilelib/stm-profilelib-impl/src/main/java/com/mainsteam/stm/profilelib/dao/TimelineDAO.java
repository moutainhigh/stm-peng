package com.mainsteam.stm.profilelib.dao;

import java.util.List;

import com.mainsteam.stm.profilelib.po.TimelinePO;

/**
 * 
 * @author xiaoruqiang
 */
public interface TimelineDAO {
	
	/**
	 * 通过timelineId 获取timeline信息
	 * @param timelineId 
	 * @return
	 */
	public TimelinePO getTimelineById(long timelineId) throws Exception;
	
	/**
	 * 获取所有的基线
	 * @return
	 * @throws Exception
	 */
	public List<TimelinePO> getAllTimeline() throws Exception;
	
	/**
	 * 通过通过策略Id获取timeline信息
	 * @param profileId
	 * @return
	 */
	public List<TimelinePO> getTimelineByProfileId(long profileId) throws Exception;
	
	/**
	 * 删除一条基线
	 * @param timelineId
	 * @return
	 */
	public int removeTimelineById(long timelineId) throws Exception;
	
	/**
	 * 通过策略删除基线
	 * @param profileId
	 * @return 
	 */
	public int removeTimelineByProfileId(long profileId) throws Exception;
	
	/**
	 * 更新基线信息
	 * @param timelinePO
	 * @return
	 */
	public int updateTimeline(TimelinePO timelinePO) throws Exception;
	
	/**
	 * 更新基线信息
	 * @param timelinePOs
	 * @return
	 */
	public int updateTimelines(List<TimelinePO> timelinePOs) throws Exception;
	
	/**
	 * 新增基线信息
	 * @param timelinePO
	 * @return
	 */
	public int insertTimeline(TimelinePO timelinePO) throws Exception;
	
	/**
	 * 新增加基线信息
	 * @param timelinePOs
	 * @return
	 */
	public int insertTimelines(List<TimelinePO> timelinePOs) throws Exception;
}
