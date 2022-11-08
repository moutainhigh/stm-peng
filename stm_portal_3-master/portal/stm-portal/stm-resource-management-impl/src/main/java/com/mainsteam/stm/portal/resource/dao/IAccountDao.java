package com.mainsteam.stm.portal.resource.dao;


import java.util.List;
import java.util.Set;

import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.portal.resource.po.AccountPo;

public interface IAccountDao {
	int insert(AccountPo accountPo);

	int del(long id);

	int update(AccountPo accountPo);

	AccountPo get(long id);

	List<AccountPo> getList(Set<IDomain> domains);
	
	int countAccountByDomainId(long domainId);
}
