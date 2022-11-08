package com.mainsteam.stm.ct.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.ct.bo.MsAverageWeb;
import com.mainsteam.stm.ct.dao.AverageWebMapper;
import com.mainsteam.stm.platform.dao.BaseDao;

public class AverageWebMapperImpl extends BaseDao<MsAverageWeb> implements AverageWebMapper {

	public AverageWebMapperImpl(SqlSessionTemplate session) {
		super(session, AverageWebMapper.class.getName());
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<MsAverageWeb> getList(MsAverageWeb msAverageWeb) {
		// TODO Auto-generated method stub
		return super.select("getList", msAverageWeb);
	}
	@Override
	public int insert(MsAverageWeb msAverageWeb) {
		// TODO Auto-generated method stub
		return super.insert(msAverageWeb);
	}
}
