package com.mainsteam.stm.portal.netflow.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.netflow.bo.ConfLicense;
import com.mainsteam.stm.portal.netflow.dao.IConfLicenseDao;

public class ConfLicenseDaoImpl extends BaseDao<ConfLicense> implements
		IConfLicenseDao {

	public ConfLicenseDaoImpl(SqlSessionTemplate session) {
		super(session, IConfLicenseDao.class.getName());
	}

	@Override
	public ConfLicense getLicense(long id) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		return super.getSession().selectOne("netflow_getLicense", map);
	}

	@Override
	public int updateLicense(ConfLicense confLicense) {
		return super.getSession().update("netflow_updateLicense", confLicense);
	}

}
