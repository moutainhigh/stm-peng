package com.mainsteam.stm.system.um.relation.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.system.um.relation.bo.DomainRole;
import com.mainsteam.stm.system.um.relation.bo.UmRelation;
import com.mainsteam.stm.system.um.relation.bo.UserDomain;
import com.mainsteam.stm.system.um.relation.bo.UserRole;
import com.mainsteam.stm.system.um.relation.dao.IUmRelationDao;

public class UmRelationDaoImpl extends BaseDao<UmRelation> implements IUmRelationDao{

	public UmRelationDaoImpl(SqlSessionTemplate session) {
		super(session, IUmRelationDao.class.getName());
		
	}

	@Override
	public List<UserRole> getUserRole() {
		
		return getSession().selectList(getNamespace()+"getUserRole");
	}

	@Override
	public List<UserRole> getUserRoleByDomainId(UserDomain userDomain) {
		
		return getSession().selectList(getNamespace()+"getUserRoleByDomainId", userDomain);
	}

	

	@Override
	public List<UserDomain> getUserDomain() {
		
		return getSession().selectList(getNamespace()+"getUserDomain");
	}

	@Override
	public List<UserDomain> getUserDomainByRoleId(Long roleId) {
		
		return getSession().selectList(getNamespace()+"getUserDomainByRoleId", roleId);
	}

	@Override
	public List<DomainRole> getDomainRole() {
		
		return getSession().selectList(getNamespace()+"getDomainRole");
	}

	@Override
	public List<DomainRole> getDomainRoleByUserId(Long userId) {
		
		return getSession().selectList(getNamespace()+"getDomainRoleByUserId", userId);
	}

	@Override
	public int batchDel(List<UmRelation> umRelations) {
		int count=0;
		for (UmRelation umRelation : umRelations) {
			count+=getSession().delete(getNamespace()+"batchDel", umRelation);
		}
		return count;
	}

	@Override
	public List<UserRole> getUserRoleForAdmin() {
		
		return getSession().selectList(getNamespace()+"getUserRoleForAdmin");
	}

	@Override
	public List<DomainRole> getDomainRoleForAdmin() {
		
		return getSession().selectList(getNamespace()+"getDomainRoleForAdmin");
	}

	@Override
	public List<UserDomain> getUserDomainForAdmin() {
		
		return getSession().selectList(getNamespace()+"getUserDomainForAdmin");
	}

	@Override
	public List<UmRelation> getAllUmRelations(UmRelation relation) {
		
		return getSession().selectList(getNamespace()+"getAllUmRelations",relation);
	}


}
