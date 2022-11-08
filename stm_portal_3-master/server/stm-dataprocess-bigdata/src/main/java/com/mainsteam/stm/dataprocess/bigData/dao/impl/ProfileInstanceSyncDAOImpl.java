package com.mainsteam.stm.dataprocess.bigData.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.dataprocess.bigData.dao.ProfileInstanceSyncDAO;
import com.mainsteam.stm.dataprocess.bigData.dao.pojo.ProfileInstanceDO;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public class ProfileInstanceSyncDAOImpl implements ProfileInstanceSyncDAO {

	private SqlSession session;
	
	public void setSession(SqlSession session) {
		this.session = session;
	}

	@Override
	public List<ProfileInstanceDO> getAllProfileInstance(
			Page<ProfileInstanceDO, ProfileInstanceDO> page) throws Exception {
		return session.selectList("getAllProfileInstance",page);
	}
}
