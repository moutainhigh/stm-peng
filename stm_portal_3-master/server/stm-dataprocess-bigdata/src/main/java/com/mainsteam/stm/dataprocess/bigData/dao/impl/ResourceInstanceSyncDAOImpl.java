package com.mainsteam.stm.dataprocess.bigData.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.dataprocess.bigData.dao.ResourceIntanceSyncDAO;
import com.mainsteam.stm.dataprocess.bigData.dao.pojo.ResourceInstanceSyncDO;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public class ResourceInstanceSyncDAOImpl implements ResourceIntanceSyncDAO {

	private SqlSession session;
	
	public void setSession(SqlSession session) {
		this.session = session;
	}
	
	@Override
	public List<ResourceInstanceSyncDO> getAllResourceInstance(Page<ResourceInstanceSyncDO,ResourceInstanceSyncDO> page) {
		return session.selectList("getAllResourceInstance",page);
	}
}
