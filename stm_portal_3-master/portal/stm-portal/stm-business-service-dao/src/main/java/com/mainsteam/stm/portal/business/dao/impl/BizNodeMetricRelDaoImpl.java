package com.mainsteam.stm.portal.business.dao.impl;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.business.bo.BizNodeMetricRelBo;
import com.mainsteam.stm.portal.business.dao.IBizNodeMetricRelDao;

public class BizNodeMetricRelDaoImpl extends BaseDao<BizNodeMetricRelBo> implements IBizNodeMetricRelDao {

	public BizNodeMetricRelDaoImpl(SqlSessionTemplate session,
			String iDaoNamespace) {
		super(session, iDaoNamespace);
		// TODO Auto-generated constructor stub
	}

}
