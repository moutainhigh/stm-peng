package com.mainsteam.stm.portal.threed.dao.impl;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.threed.bo.UrlBo;
import com.mainsteam.stm.portal.threed.dao.IUrlDao;

public class UrlDaoImpl extends BaseDao<UrlBo> implements IUrlDao{

	public UrlDaoImpl(SqlSessionTemplate session) {
		super(session, IUrlDao.class.getName());
	}

	@Override
	public int add3DInfo(UrlBo bo) {
		return getSession().insert("add3DInfo", bo);
	}

	@Override
	public UrlBo get3DInfo() {
		return getSession().selectOne("get3DInfo");
	}
}
