package com.mainsteam.stm.ct.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.ct.bo.MsCtMetrics;
import com.mainsteam.stm.ct.dao.CtMetricsMapper;
import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public class CtMetricsMapperImpl extends BaseDao<MsCtMetrics> implements CtMetricsMapper {

	public CtMetricsMapperImpl(SqlSessionTemplate session) {
		super(session, CtMetricsMapper.class.getName());
		// TODO Auto-generated constructor stub
	}

	@Override
	public void getCtMetrics(Page<MsCtMetrics, MsCtMetrics> page) {
		// TODO Auto-generated method stub
		this.select("getCtMetrics", page);
	}

	@Override
	public MsCtMetrics getByName(String name) {
		// TODO Auto-generated method stub;
		List<MsCtMetrics> select = this.select("getByName", name);
		if(select.size()>0){
			return select.get(0);
		}
		return null;
	}

	@Override
	public MsCtMetrics selectById(Integer id) {
		// TODO Auto-generated method stub
		return this.getSession().selectOne("selectById", id);
	}

}
