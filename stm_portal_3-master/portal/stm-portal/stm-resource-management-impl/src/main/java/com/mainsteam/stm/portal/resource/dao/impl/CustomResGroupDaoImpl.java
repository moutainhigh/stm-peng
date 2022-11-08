package com.mainsteam.stm.portal.resource.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.resource.bo.Wbh4HomeBo;
import com.mainsteam.stm.portal.resource.dao.ICustomResGroupDao;

public class CustomResGroupDaoImpl extends BaseDao<Wbh4HomeBo> implements ICustomResGroupDao {

	public CustomResGroupDaoImpl(SqlSessionTemplate session) {
		super(session, ICustomResGroupDao.class.getName());
	}

	@Override
	public List<Wbh4HomeBo> getWbh4HomeLikeGroupId(long groupId) {
		return getSession().selectList("getWbh4HomeLikeGroupId", groupId);
	}

	@Override
	public int updateWbh4HomeSelfExtByPrimary(Wbh4HomeBo wbh4Home) {
		return getSession().update("updateWbh4HomeSelfExtByPrimary", wbh4Home);
	}
}
