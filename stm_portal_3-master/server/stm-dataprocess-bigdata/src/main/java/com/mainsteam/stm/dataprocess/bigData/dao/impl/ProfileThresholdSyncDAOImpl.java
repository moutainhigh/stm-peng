package com.mainsteam.stm.dataprocess.bigData.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.dataprocess.bigData.dao.ProfileThresholdSyncDAO;
import com.mainsteam.stm.dataprocess.bigData.dao.pojo.ProfileThresholdSyncDO;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public class ProfileThresholdSyncDAOImpl implements ProfileThresholdSyncDAO {

	private SqlSession session;
	
	public void setSession(SqlSession session) {
		this.session = session;
	}

	@Override
	public List<ProfileThresholdSyncDO> getAllThreshold(
			Page<ProfileThresholdSyncDO, ProfileThresholdSyncDO> page)
			throws Exception {
		return session.selectList("getAllThreshold",page);
	}
}
