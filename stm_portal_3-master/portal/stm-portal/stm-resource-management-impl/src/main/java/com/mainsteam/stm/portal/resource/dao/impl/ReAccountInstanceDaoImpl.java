package com.mainsteam.stm.portal.resource.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.resource.dao.IReAccountInstanceDao;
import com.mainsteam.stm.portal.resource.po.ReAccountInstancePo;

public class ReAccountInstanceDaoImpl extends BaseDao<ReAccountInstancePo>
		implements IReAccountInstanceDao {

	public ReAccountInstanceDaoImpl(SqlSessionTemplate session) {
		super(session, IReAccountInstanceDao.class.getName());
	}

	@Override
	public int insert(ReAccountInstancePo reAccountInstancePo) {
		return super.insert(reAccountInstancePo);
	}

	@Override
	public List<ReAccountInstancePo> getList() {
		return getSession().selectList(getNamespace() + "getList");
	}

	@Override
	public List<ReAccountInstancePo> getByAccountId(long accountId) {
		return getSession().selectList(getNamespace() + "getByAccountId", accountId);
	}

	@Override
	public int deleteResourceAndAccountRelation(long[] instanceIds) {
		return getSession().delete(getNamespace() + "deleteResourceAndAccountRelationById", instanceIds);
	}

}
