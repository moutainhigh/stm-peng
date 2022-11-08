package com.mainsteam.stm.portal.business.dao.impl;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.business.bo.BizEditBo;
import com.mainsteam.stm.portal.business.dao.IBizEditDao;

public class BizEditDaoImpl extends BaseDao<BizEditBo> implements IBizEditDao {

	public BizEditDaoImpl(SqlSessionTemplate session) {
		super(session, IBizEditDao.class.getName());
		// TODO Auto-generated constructor stub
	}

	@Override
	public int updateBizMetricValue(BizEditBo edit) {
		// TODO Auto-generated method stub
		return getSession().update(getNamespace() + "updateBizMetricValue", edit);
	}

	@Override
	public int deleteBizMetricValue(BizEditBo edit) {
		// TODO Auto-generated method stub
		return getSession().delete(getNamespace() + "deleteBizMetricValue", edit);
	}

	@Override
	public String getBizMeticValue(BizEditBo edit) {
		// TODO Auto-generated method stub
		return getSession().selectOne(getNamespace() + "getBizMeticValue",edit);
	}

	@Override
	public int insertBizMetricValue(BizEditBo edit) {
		// TODO Auto-generated method stub
		return getSession().insert(getNamespace() + "insertBizMetricValue",edit);
	}

}
