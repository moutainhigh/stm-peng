package com.mainsteam.stm.portal.business.dao.impl;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.business.bo.BizAlarmInfoBo;
import com.mainsteam.stm.portal.business.dao.IBizAlarmInfoDao;

public class BizAlarmInfoDaoImpl  extends BaseDao<BizAlarmInfoBo> implements IBizAlarmInfoDao {

	public BizAlarmInfoDaoImpl(SqlSessionTemplate session) {
		super(session, IBizAlarmInfoDao.class.getName());

	}


	@Override
	public BizAlarmInfoBo getAlarmInfo(BizAlarmInfoBo infoBo) {
		// TODO Auto-generated method stub
		return getSession().selectOne(getNamespace() + "getAlarmInfo",infoBo);
	}

	@Override
	public int insertBizAlarmInfo(BizAlarmInfoBo infoBo) {
		// TODO Auto-generated method stub

	return getSession().insert(getNamespace() + "insertBizAlarmInfo", infoBo);
	}

	@Override
	public int updateBizAlarmInfo(BizAlarmInfoBo infoBo) {
		// TODO Auto-generated method stub
		return getSession().update(getNamespace() +"updateBizAlarmInfo",infoBo);
	}


	@Override
	public BizAlarmInfoBo getAlarmInfoById(long bizid) {
		// TODO Auto-generated method stub
		return getSession().selectOne(getNamespace() + "getAlarmInfoById",bizid);
	}


	@Override
	public int deleteInfo(long bizid) {
		// TODO Auto-generated method stub
		return getSession().delete(getNamespace()+"deleteInfo", bizid);
	}



}
