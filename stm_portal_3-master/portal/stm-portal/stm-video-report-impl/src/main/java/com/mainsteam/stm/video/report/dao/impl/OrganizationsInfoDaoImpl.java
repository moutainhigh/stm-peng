package com.mainsteam.stm.video.report.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.video.report.bo.Organizayions;
import com.mainsteam.stm.video.report.dao.OrganizationsInfoDao;

public class OrganizationsInfoDaoImpl extends BaseDao<Organizayions> implements OrganizationsInfoDao{

	public OrganizationsInfoDaoImpl(SqlSessionTemplate session) {
		super(session, OrganizationsInfoDao.class.getName());
	}

	@Override
	public List<Organizayions> getAllOrg() {
		return getSession().selectList(getNamespace() + "selectAllOrg");
	}

	@Override
	public List<Organizayions> getAllOrgByLevel(int pid,int level) {
		// TODO Auto-generated method stub
		Map<String, Object> map= new HashMap<String, Object>();
		map.put("pid", pid);
		map.put("level",level);
		return getSession().selectList(getNamespace() + "selectAllOrgByLevel",map);
	}

	@Override
	public List<Organizayions> getAllOrgByid(int id) {
		// TODO Auto-generated method stub
				return getSession().selectList(getNamespace() + "getAllOrgByid",id);
	}

	@Override
	public List<Organizayions> getAllOrgByPid(int pid) {
		return getSession().selectList(getNamespace() + "getAllOrgByPid",pid);
	}

}
