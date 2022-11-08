package com.mainsteam.stm.ct.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mainsteam.stm.ct.bo.MsWebsiteMetric;
import com.mainsteam.stm.ct.bo.WebsiteMetricVo;

public interface WebsiteMetricsMapper{

    List<MsWebsiteMetric> getList(WebsiteMetricVo websiteMetricVo);

    MsWebsiteMetric getAvg(WebsiteMetricVo websiteMetricVo);

    MsWebsiteMetric getAvgByResourceId(WebsiteMetricVo websiteMetricVo);

	int insert(MsWebsiteMetric msWebsiteMetric);

	MsWebsiteMetric getLatest(MsWebsiteMetric metric);
	
}
