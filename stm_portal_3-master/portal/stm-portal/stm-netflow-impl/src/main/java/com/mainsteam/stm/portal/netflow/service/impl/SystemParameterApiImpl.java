package com.mainsteam.stm.portal.netflow.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.portal.netflow.api.ISystemParameterApi;
import com.mainsteam.stm.portal.netflow.bo.SystemParameterBo;
import com.mainsteam.stm.portal.netflow.dao.ISystemParameterDao;

@Service("systemParameterApi")
public class SystemParameterApiImpl implements ISystemParameterApi {

	@Autowired
	private ISystemParameterDao systemParameterDao;

	@Override
	public SystemParameterBo getServiePort() {
		return this.systemParameterDao.getServiePort();
	}

	@Override
	public int update(int port) {
		return this.systemParameterDao.update(port);
	}

}
