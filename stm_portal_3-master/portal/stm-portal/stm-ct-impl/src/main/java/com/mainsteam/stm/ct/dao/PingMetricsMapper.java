package com.mainsteam.stm.ct.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mainsteam.stm.ct.bo.MsPingMetric;
import com.mainsteam.stm.ct.bo.WebsiteMetricVo;

public interface PingMetricsMapper{

    List<MsPingMetric> getList(WebsiteMetricVo websiteMetricVo);

	int insert(MsPingMetric msPingMetric);

	MsPingMetric getLatest(MsPingMetric pingMetric);
}
