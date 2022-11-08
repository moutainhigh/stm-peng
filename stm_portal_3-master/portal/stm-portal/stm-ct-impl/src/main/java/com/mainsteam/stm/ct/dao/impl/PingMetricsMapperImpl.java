package com.mainsteam.stm.ct.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.ct.bo.MsPingMetric;
import com.mainsteam.stm.ct.bo.WebsiteMetricVo;
import com.mainsteam.stm.ct.dao.PingMetricsMapper;
import com.mainsteam.stm.platform.dao.BaseDao;

public class PingMetricsMapperImpl extends BaseDao<MsPingMetric> implements PingMetricsMapper {
	Logger log=Logger.getLogger(PingMetricsMapperImpl.class);
	public PingMetricsMapperImpl(SqlSessionTemplate session) {
		super(session, PingMetricsMapper.class.getName());
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<MsPingMetric> getList(WebsiteMetricVo websiteMetricVo) {
		// TODO Auto-generated method stub
		List<MsPingMetric> list=null;
    	try{
    		list = super.select("getList",websiteMetricVo);
    	}catch (Exception e){
    		log.error(e.getMessage());
    	}
        return list;
		
	}
	@Override
	public int insert(MsPingMetric msPingMetric){
		
		return super.insert(msPingMetric);
	}

	@Override
	public MsPingMetric getLatest(MsPingMetric pingMetric) {
		// TODO Auto-generated method stub
		return super.getSession().selectOne(getNamespace()+"getLatest", pingMetric);
	}

}
