package com.mainsteam.stm.ipmanage.impl.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.ipmanage.bo.Depart;
import com.mainsteam.stm.ipmanage.bo.NetGroup;

import com.mainsteam.stm.ipmanage.impl.dao.NetGroupMapper;
import com.mainsteam.stm.platform.dao.BaseDao;

public class NetGroupMapperImpl extends BaseDao<Depart> implements NetGroupMapper {

	Logger logger=Logger.getLogger(NetGroupMapperImpl.class);
	public NetGroupMapperImpl(SqlSessionTemplate session) {
		super(session, NetGroupMapper.class.getName());
		
	}
	@Override
	public List<NetGroup> getNetGroupList() {
		// TODO Auto-generated method stub
		return  getSession().selectList(getNamespace()+"getNetGroupList");
	}
	@Override
	public int insertNetGroup(NetGroup netGroup) {
		// TODO Auto-generated method stub
		return getSession().insert(getNamespace()+"insertNetGroup");
	}
	@Override
	public int update(NetGroup netGroup) {
		// TODO Auto-generated method stub
		return getSession().update(getNamespace()+"update");
	}
	@Override
	public int delete(Integer id) {
		// TODO Auto-generated method stub
		return getSession().delete(getNamespace()+"delete");
	}

}
