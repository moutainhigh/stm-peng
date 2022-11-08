package com.mainsteam.stm.system.um.domain.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.system.um.domain.bo.DomainDcs;
import com.mainsteam.stm.system.um.domain.dao.IDomainDao;
import com.mainsteam.stm.system.um.relation.bo.UmRelation;
import com.mainsteam.stm.system.um.relation.bo.UserDomain;
import com.mainsteam.stm.system.um.relation.bo.UserRole;
import com.mainsteam.stm.system.um.user.bo.User;

@Repository("stm_system_DomainDao")
public class DomainDaoImpl extends BaseDao<Domain> implements IDomainDao {
	
	@Autowired
	public DomainDaoImpl(@Qualifier(SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, IDomainDao.class.getName());
	}

	@Override
	public int queryByName(String name) {
		// TODO Auto-generated method stub
		Integer id = getSession().selectOne(getNamespace() + "queryByName",
				name);
		if (id != null)
			return id;
		else {
			return 0;
		}
	}

	@Override
	public List<Domain> pageSelect(Page<Domain, User> page) {
		return getSession().selectList(getNamespace() + "pageSelect", page);
	}

	@Override
	public List<UserDomain> getDomainAdmin(UmRelation relation) {
		return getSession().selectList(getNamespace()+"getDomainAdmin", relation);
	}
	@Override
	public List<UserRole> getUserRolesByDomainId(Long domainId) {
		return getSession().selectList(getNamespace()+"getUserRoleByDomainId", domainId);
	}

	@Override
	public List<DomainDcs> getDomainDcsRel(long domainId) {
		return getSession().selectList(getNamespace()+"getDomainDcsRel", domainId);
	}

	@Override
	public int batchInsertDomainDcsRel(List<DomainDcs> domainDcs) {
		SqlSession session=getSession();
		int count=0;
		Object[] os=domainDcs.toArray();
		for(int i=0,size=os.length;i<size;i++){
			count+=session.update(getNamespace()+"batchInsertDomainDcsRel", os[i]);
		}
		return count;
	}

	@Override
	public int batchDeleteDomainDcsRel(long domainId) {
		return getSession().delete("batchDeleteDomainDcsRel",domainId);
	}

	@Override
	public List<Domain> getAllDomains() {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace()+"getAllDomains");
	}

	@Override
	public List<Domain> getDomains(Long userId,Long roleId) {
		UmRelation ur=new UmRelation();
		ur.setUserId(userId);
		ur.setRoleId(roleId);
		return select("getDomains", ur);
	}

	@Override
	public List<User> queryDomainUsers(Long[] ids) {
		return getSession().selectList(getNamespace()+"queryDomainUsers", ids);
	}
}
