package com.mainsteam.stm.ct.dao;

import com.mainsteam.stm.ct.bo.MsMetricTemplates;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public interface MetricTemplatesMapper {

    public void getMetricTemplates(Page<MsMetricTemplates,MsMetricTemplates> page);

    int deleteTemplatesByProfilelibId(String profilelibId);

    int insertTemplates(MsMetricTemplates msMetricTemplates);

	public int insert(MsMetricTemplates msMetricTemplates);

	public int updateById(MsMetricTemplates metricTemplate);
}
