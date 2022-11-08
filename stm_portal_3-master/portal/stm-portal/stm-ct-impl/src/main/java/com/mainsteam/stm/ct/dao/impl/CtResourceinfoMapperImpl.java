package com.mainsteam.stm.ct.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.ct.bo.MsCtResourceinfo;
import com.mainsteam.stm.ct.dao.CtResourceinfoMapper;
import com.mainsteam.stm.platform.dao.BaseDao;

public class CtResourceinfoMapperImpl extends BaseDao<MsCtResourceinfo> implements CtResourceinfoMapper {

	public CtResourceinfoMapperImpl(SqlSessionTemplate session) {
		super(session, CtResourceinfoMapper.class.getName());
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public List<MsCtResourceinfo> selectById(String id) {
		// TODO Auto-generated method stub
		return super.select("selectById", id);
	}

	@Override
	public void addInfo(MsCtResourceinfo resourceinfo) {
		// TODO Auto-generated method stub
		super.insert("insert",resourceinfo);
	}

}
