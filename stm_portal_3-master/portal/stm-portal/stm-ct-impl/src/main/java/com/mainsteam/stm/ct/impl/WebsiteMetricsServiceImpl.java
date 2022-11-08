package com.mainsteam.stm.ct.impl;



import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.logging.Log;
import com.mainsteam.stm.ct.api.IWebsiteMetricService;
import com.mainsteam.stm.ct.bo.MsWebsiteMetric;
import com.mainsteam.stm.ct.bo.WebsiteMetricVo;
import com.mainsteam.stm.ct.dao.WebsiteMetricsMapper;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

import java.util.Date;
import java.util.List;


public class WebsiteMetricsServiceImpl implements IWebsiteMetricService {
	Logger logger=Logger.getLogger(WebsiteMetricsServiceImpl.class);
    @Autowired
    private WebsiteMetricsMapper websiteMetricsMapper;

   

    @Override
    public int insertWebsiteMetric(MsWebsiteMetric msWebsiteMetric) {
        return websiteMetricsMapper.insert(msWebsiteMetric);
    }

    @Override
    public List<MsWebsiteMetric> getList(WebsiteMetricVo websiteMetricVo) {
        return websiteMetricsMapper.getList(websiteMetricVo);
    }

    @Override
    public MsWebsiteMetric getAvg(WebsiteMetricVo websiteMetricVo) {
    	logger.error(websiteMetricVo.getStartTime()+"\t"+websiteMetricVo.getEndTime());
        MsWebsiteMetric msWebsiteMetric=null;
		try {
			msWebsiteMetric=websiteMetricsMapper.getAvg(websiteMetricVo);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			logger.error(e.getStackTrace());
			logger.error(e);
		}
		return msWebsiteMetric;
    }

    @Override
    public MsWebsiteMetric getAvgByResourceId(WebsiteMetricVo websiteMetricVo) {
        return websiteMetricsMapper.getAvgByResourceId(websiteMetricVo);
    }

	@Override
	public void getWebsiteMetricPage(Page<MsWebsiteMetric, MsWebsiteMetric> page) {
		
		
	}

	@Override
	public MsWebsiteMetric getLatest(String id) {
		// TODO Auto-generated method stub
		MsWebsiteMetric metric=new MsWebsiteMetric();
		metric.setResource_id(id);
		return websiteMetricsMapper.getLatest(metric);
	}
}
