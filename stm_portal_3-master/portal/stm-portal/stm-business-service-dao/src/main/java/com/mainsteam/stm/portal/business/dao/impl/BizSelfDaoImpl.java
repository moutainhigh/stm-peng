package com.mainsteam.stm.portal.business.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.business.bo.BizDepBo;
import com.mainsteam.stm.portal.business.bo.BizSelfBo;
import com.mainsteam.stm.portal.business.dao.IBizSelfDao;

public class BizSelfDaoImpl extends BaseDao<BizDepBo> implements IBizSelfDao{

	public BizSelfDaoImpl(SqlSessionTemplate session) {
		super(session, IBizSelfDao.class.getName());
	}

	@Override
	public int insert(BizSelfBo bizSelfBo) {
		return getSession().insert(getNamespace() +"insert",bizSelfBo);
	}

	@Override
	public List<BizSelfBo> getList() {
		return getSession().selectList(getNamespace()+"getList");
	}

	@Override
	public void deleteById(long id) {
		getSession().delete(getNamespace()+"deleteById", id);
	}

	@Override
	public List<BizSelfBo> getListByIds(List<Long> ids) {
		return getSession().selectList(getNamespace()+"getListByIds",ids);
	}
}
