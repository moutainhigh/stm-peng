package com.mainsteam.stm.ct.impl;


import org.springframework.beans.factory.annotation.Autowired;
import com.mainsteam.stm.ct.api.ICtMetricsService;
import com.mainsteam.stm.ct.bo.MsCtMetrics;
import com.mainsteam.stm.ct.dao.CtMetricsMapper;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public class CtMetricsServiceImpl implements ICtMetricsService {

    @Autowired
    private CtMetricsMapper ctMetricsMapper;

	@Override
	public void getAllCtMetrics(Page<MsCtMetrics, MsCtMetrics> page) {
		// TODO Auto-generated method stub
		ctMetricsMapper.getCtMetrics(page);
	}

	@Override
	public MsCtMetrics selectOne(Integer id) {
		// TODO Auto-generated method stub
		return ctMetricsMapper.selectById(id);
	}

	@Override
	public MsCtMetrics selectByName(String name) {
		// TODO Auto-generated method stub
		return ctMetricsMapper.getByName(name);
	}
}
