package com.mainsteam.stm.portal.resource.dao.impl;


import java.util.List;
import java.util.Set;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.portal.resource.dao.IAccountDao;
import com.mainsteam.stm.portal.resource.po.AccountPo;

public class AccountDaoImpl extends BaseDao<AccountPo> implements IAccountDao {
	
	public AccountDaoImpl(SqlSessionTemplate session) {
		super(session, IAccountDao.class.getName());
	}

	@Override
	public List<AccountPo> getList(Set<IDomain> domains) {
		long[] ids = new long[domains.size()];
		int i = 0;
		for(IDomain domain : domains){
			ids[i] = domain.getId();
			i++;
		}
		return getSession().selectList(getNamespace() + "getList", ids);
	}

	@Override
	public int countAccountByDomainId(long domainId) {
		return getSession().selectOne(getNamespace() + "getAccountCountByDomainId", domainId);
	}

}
