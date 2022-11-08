package com.mainsteam.stm.portal.extendedplatform.resourceprofile.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.portal.extendedplatform.resourceprofile.api.InstanceService;
import com.mainsteam.stm.portal.extendedplatform.resourceprofile.dao.InstanceDao;
import com.mainsteam.stm.portal.extendedplatform.resourceprofile.po.ProfileInstancePo;

@Service("extendInstanceService")
public class InstanceServiceImpl implements InstanceService {

	@Autowired
	@Qualifier("extendInstanceDao")
	private InstanceDao instanceDao;
	
	@Override
	public List<ResourceInstance> queryInstanceById(long id) {
		return instanceDao.queryInstanceById(id);
	}

	@Override
	public List<ProfileInstancePo> queryProfileInstancePos(long instanceId) {
		return instanceDao.queryProfileInstancePos(instanceId);
	}

	@Override
	public List<ProfileInstancePo> queryProfileInstanceLastPos(long instanceId) {
		return instanceDao.queryProfileInstanceLastPos(instanceId);
	}

	@Override
	public int deleteProfileInstanceRel(long instanceId) {
		return instanceDao.deleteProfileInstanceRel(instanceId);
	}

	@Override
	public int deleteProfileInstanceLastRel(long instanceId) {
		return instanceDao.deleteProfileInstanceLastRel(instanceId);
	}

	@Override
	public int deleteInstance(long instanceId) {
		return instanceDao.deleteInstance(instanceId);
	}

}
