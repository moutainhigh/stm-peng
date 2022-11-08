package com.mainsteam.stm.ct.dao.impl;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.ct.bo.MsMetricTemplates;
import com.mainsteam.stm.ct.dao.MetricTemplatesMapper;
import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public class MetricTemplatesMapperImpl extends BaseDao<MsMetricTemplates> implements MetricTemplatesMapper {

	public MetricTemplatesMapperImpl(SqlSessionTemplate session) {
		super(session, MetricTemplatesMapper.class.getName());
		// TODO Auto-generated constructor stub
	}

	@Override
	public void getMetricTemplates(Page<MsMetricTemplates, MsMetricTemplates> page) {
		// TODO Auto-generated method stub
		this.select("getMetricTemplates",page);
	}

	@Override
	public int deleteTemplatesByProfilelibId(String profilelibId) {
		// TODO Auto-generated method stub
		return super.del("deleteTemplatesByProfilelibId", profilelibId);
	}

	@Override
	public int insertTemplates(MsMetricTemplates msMetricTemplates) {
		// TODO Auto-generated method stub
		return super.insert(msMetricTemplates);
	}

	@Override
	public int updateById(MsMetricTemplates metricTemplate) {
		// TODO Auto-generated method stub
		return super.update(metricTemplate);
	}

}
