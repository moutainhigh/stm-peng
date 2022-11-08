package com.mainsteam.stm.portal.extendedplatform.resourceprofile.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.instancelib.obj.InstanceProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.portal.extendedplatform.resourceprofile.dao.InstanceDao;
import com.mainsteam.stm.portal.extendedplatform.resourceprofile.po.ProfileInstancePo;

public class InstanceDaoImpl implements InstanceDao {

	private SqlSession session;
	public void setSession(SqlSession session) {
		this.session = session;
	}
	
	@Override
	public List<ResourceInstance> queryInstanceById(long id) {
		return session.selectList("queryInstanceById",id);
	}

	@Override
	public List<ProfileInstancePo> queryProfileInstancePos(long instanceId) {
		return session.selectList("queryProfileInstancePos",instanceId);
	}

	@Override
	public List<ProfileInstancePo> queryProfileInstanceLastPos(long instanceId) {
		return session.selectList("queryProfileInstanceLastPos",instanceId);
	}

	@Override
	public int deleteProfileInstanceRel(long instanceId) {
		return session.delete("com.mainsteam.stm.portal.extendedplatform.resourceprofile.dao.InstanceDao.deleteProfileInstanceRel", instanceId);
	}

	@Override
	public int deleteProfileInstanceLastRel(long instanceId) {
		return session.delete("com.mainsteam.stm.portal.extendedplatform.resourceprofile.dao.InstanceDao.deleteProfileInstanceLastRel", instanceId);
	}

	@Override
	public int deleteInstance(long instanceId) {
		return session.delete("deleteInstance",instanceId);
	}


}
