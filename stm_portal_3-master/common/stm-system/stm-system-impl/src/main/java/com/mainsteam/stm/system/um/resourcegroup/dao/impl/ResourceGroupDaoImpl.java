package com.mainsteam.stm.system.um.resourcegroup.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.system.um.resourcegroup.bo.DomainResourceGroupRel;
import com.mainsteam.stm.system.um.resourcegroup.dao.IResourceGroupDao;

@Repository
public class ResourceGroupDaoImpl extends BaseDao<DomainResourceGroupRel> implements
		IResourceGroupDao {

	@Autowired
	public ResourceGroupDaoImpl(@Qualifier(SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, IResourceGroupDao.class.getName());
	}

	@Override
	public List<DomainResourceGroupRel> queryRelByDomain(long domainId) {
		return getSession().selectList(getNamespace()+"queryRelByDomain", domainId);
	}

	@Override
	public List<DomainResourceGroupRel> pageSelect(
			Page<DomainResourceGroupRel, DomainResourceGroupRel> page) {
		return getSession().selectList(getNamespace()+"pageSelect", page);
	}
}
