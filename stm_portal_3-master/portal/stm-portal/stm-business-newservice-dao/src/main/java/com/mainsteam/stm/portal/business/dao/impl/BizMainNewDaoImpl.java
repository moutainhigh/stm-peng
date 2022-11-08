package com.mainsteam.stm.portal.business.dao.impl;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.business.bo.BizMainNewBo;
import com.mainsteam.stm.portal.business.dao.IBizMainNewDao;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BizMainNewDaoImpl extends BaseDao<BizMainNewBo> implements IBizMainNewDao {

	public BizMainNewDaoImpl(SqlSessionTemplate session) {
		super(session, IBizMainNewDao.class.getName());
		// TODO Auto-generated constructor stub
	}

	@Override
	public BizMainNewBo getDefaultView() {
		return null;
	}

}
