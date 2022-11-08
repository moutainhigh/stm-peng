package com.mainsteam.stm.ct.api;

import com.mainsteam.stm.ct.bo.MsCtAlarm;
import com.mainsteam.stm.ct.bo.MsCtMetrics;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public interface ICtMetricsService {

    void getAllCtMetrics(Page<MsCtMetrics,MsCtMetrics> page);

    MsCtMetrics selectOne(Integer id);

    MsCtMetrics selectByName(String name);
}
