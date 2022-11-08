package com.mainsteam.stm.ct.impl;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.mainsteam.stm.ct.api.IPingMetricService;
import com.mainsteam.stm.ct.bo.MsPingMetric;
import com.mainsteam.stm.ct.bo.WebsiteMetricVo;
import com.mainsteam.stm.ct.dao.PingMetricsMapper;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

import java.util.List;

public class PingMetricsServiceImpl implements IPingMetricService {
	Logger log=Logger.getLogger(PingMetricsServiceImpl.class);
    @Autowired
    private PingMetricsMapper pingMetricsMapper;

    @Override
    public List<MsPingMetric> getList(WebsiteMetricVo websiteMetricVo) {
    	List<MsPingMetric> list=null;
    	try{
    		list = pingMetricsMapper.getList(websiteMetricVo);
    	}catch (Exception e){
    		log.error(e.getMessage());
    	}
        return list;
    }

	@Override
	public void getPingMetricPage(Page<MsPingMetric, MsPingMetric> page) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int insertPingMetric(MsPingMetric msPingMetric) {
		// TODO Auto-generated method stub
		return pingMetricsMapper.insert(msPingMetric);
	}

	@Override
	public MsPingMetric getLatest(String id) {
		// TODO Auto-generated method stub
		MsPingMetric pingMetric=new MsPingMetric();
		pingMetric.setResource_id(id);
		return pingMetricsMapper.getLatest(pingMetric);
	}
}
