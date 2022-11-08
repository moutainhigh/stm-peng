package com.mainsteam.stm.ipmanage.impl.dao.impl;

import java.util.List;

import com.mainsteam.stm.ipmanage.bo.Depart;
import com.mainsteam.stm.ipmanage.impl.dao.DepartMapper;
import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;

public class DepartMapperImpl extends BaseDao<Depart> implements DepartMapper {

	Logger logger=Logger.getLogger(DepartMapperImpl.class);
	public DepartMapperImpl(SqlSessionTemplate session) {
		super(session, DepartMapper.class.getName());
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Depart> getDepartList() {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace()+"getDepartList");
	}          

	@Override
	public int insert(Depart depart) {
		// TODO Auto-generated method stub
		return super.insert(depart);
	}

	@Override
	public int delete(Integer id) {
		// TODO Auto-generated method stub
		return super.del(id);
	}

	@Override
	public Depart findIdByName(String depart) {
		// TODO Auto-generated method stub
		return getSession().selectOne(getNamespace()+"findIdByName", depart);
	}

	
}
