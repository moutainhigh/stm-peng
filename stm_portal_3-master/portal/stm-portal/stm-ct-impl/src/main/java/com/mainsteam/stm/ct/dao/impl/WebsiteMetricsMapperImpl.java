package com.mainsteam.stm.ct.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;

import com.alibaba.druid.support.logging.Log;
import com.mainsteam.stm.ct.bo.MsWebsiteMetric;
import com.mainsteam.stm.ct.bo.WebsiteMetricVo;
import com.mainsteam.stm.ct.dao.WebsiteMetricsMapper;
import com.mainsteam.stm.platform.dao.BaseDao;

public class WebsiteMetricsMapperImpl extends BaseDao<MsWebsiteMetric> implements WebsiteMetricsMapper {
	Logger logger =Logger.getLogger(WebsiteMetricsMapperImpl.class);
	public WebsiteMetricsMapperImpl(SqlSessionTemplate session) {
		super(session, WebsiteMetricsMapper.class.getName());
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<MsWebsiteMetric> getList(WebsiteMetricVo websiteMetricVo) {
		// TODO Auto-generated method stub
		return super.select("getList",websiteMetricVo);
	}

	@Override
	public MsWebsiteMetric getAvg(WebsiteMetricVo websiteMetricVo) {
		// TODO Auto-generated method stub
		MsWebsiteMetric msWebsiteMetric=null;
		try {
			msWebsiteMetric=super.getSession().selectOne("getAvg",websiteMetricVo);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			logger.error(e.getStackTrace());
			logger.error(e);
		}
		return  msWebsiteMetric;
	}

	@Override
	public MsWebsiteMetric getAvgByResourceId(WebsiteMetricVo websiteMetricVo) {
		// TODO Auto-generated method stub
		return super.getSession().selectOne("getAvgByResourceId",websiteMetricVo);
	}

	@Override
	public int insert(MsWebsiteMetric msWebsiteMetric) {
		// TODO Auto-generated method stub
		return super.insert(msWebsiteMetric);
	}

	@Override
	public MsWebsiteMetric getLatest(MsWebsiteMetric metric) {
		// TODO Auto-generated method stub
		return super.getSession().selectOne(getNamespace()+"getLatest", metric);
	}

}
