package com.mainsteam.stm.portal.threed.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.threed.bo.ModelBo;
import com.mainsteam.stm.portal.threed.dao.IModelDao;

public class ModelDaoImpl extends BaseDao<ModelBo> implements IModelDao{
	
	public ModelDaoImpl(SqlSessionTemplate session) {
		super(session, IModelDao.class.getName());
	}

	@Override
	public List<ModelBo> getAll() {
		return getSession().selectList("getAllModel");
	}
	
}
