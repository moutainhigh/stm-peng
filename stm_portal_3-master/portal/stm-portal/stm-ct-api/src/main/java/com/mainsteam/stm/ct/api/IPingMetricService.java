package com.mainsteam.stm.ct.api;

import java.util.List;

import com.mainsteam.stm.ct.bo.MsPingMetric;
import com.mainsteam.stm.ct.bo.WebsiteMetricVo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public interface IPingMetricService {

    void getPingMetricPage(Page<MsPingMetric, MsPingMetric>  page);

    int insertPingMetric(MsPingMetric msPingMetric);

    List<MsPingMetric> getList(WebsiteMetricVo websiteMetricVo);

	MsPingMetric getLatest(String id);

}
