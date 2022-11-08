package com.mainsteam.stm.profilelib.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.profilelib.dao.TimelineDAO;
import com.mainsteam.stm.profilelib.po.TimelinePO;

public class TimelineDAOImpl implements TimelineDAO {

	
	private SqlSession session;

	public void setSession(SqlSession session) {
		this.session = session;
	}
	@Override
	public TimelinePO getTimelineById(long timelineId) {
		return session.selectOne("getTimelineById", timelineId);
	}

	@Override
	public List<TimelinePO> getTimelineByProfileId(long profileId) {
		return session.selectList("getTimelineByProfileId",profileId);
	}

	@Override
	public int removeTimelineById(long timelineId) {
		return session.delete("removeTimelineById",timelineId);
	}

	@Override
	public int removeTimelineByProfileId(long profileId) {
		return session.delete("removeTimelineByProfileId",profileId);
	}

	@Override
	public int updateTimeline(TimelinePO timelinePO) {
		return session.update("updateTimeline",timelinePO);
	}

	@Override
	public int updateTimelines(List<TimelinePO> timelinePOs) {
		for (TimelinePO timelinePO : timelinePOs) {
			updateTimeline(timelinePO);
		}
		return 0;
	}

	@Override
	public int insertTimeline(TimelinePO timelinePO) {
		return session.insert("insertTimeline", timelinePO); 
	}

	@Override
	public int insertTimelines(List<TimelinePO> timelinePOs) {
		for (TimelinePO timelinePO : timelinePOs) {
			insertTimeline(timelinePO);
		}
		return 0;
	}
	@Override
	public List<TimelinePO> getAllTimeline() throws Exception {
		return session.selectList("getAllTimeline");
	}
}
