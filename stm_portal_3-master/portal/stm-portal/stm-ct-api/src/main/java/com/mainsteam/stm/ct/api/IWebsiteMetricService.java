package com.mainsteam.stm.ct.api;

import java.util.List;

import com.mainsteam.stm.ct.bo.MsWebsiteMetric;
import com.mainsteam.stm.ct.bo.WebsiteMetricVo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public interface IWebsiteMetricService {

    void getWebsiteMetricPage(Page<MsWebsiteMetric, MsWebsiteMetric> page);

    int insertWebsiteMetric(MsWebsiteMetric msWebsiteMetric);

    List<MsWebsiteMetric> getList(WebsiteMetricVo websiteMetricVo);

    MsWebsiteMetric getAvg(WebsiteMetricVo websiteMetricVo);

    MsWebsiteMetric getAvgByResourceId(WebsiteMetricVo websiteMetricVo);

	MsWebsiteMetric getLatest(String id);
}
