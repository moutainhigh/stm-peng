package com.mainsteam.stm.portal.business.service.impl;

import javax.annotation.Resource;

import com.mainsteam.stm.portal.business.api.BizHealthHisApi;
import com.mainsteam.stm.portal.business.dao.IBizHealthHisDao;

public class BizHealthHisImpl implements BizHealthHisApi {

	@Resource
	private IBizHealthHisDao bizHealthHisDao;
	
	@Override
	public int getBizStatus(long id) {
		// TODO Auto-generated method stub
		return bizHealthHisDao.getBizStatus(id);
	}

}
