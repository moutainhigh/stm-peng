package com.mainsteam.stm.ct.dao;

import org.apache.ibatis.annotations.Param;

import com.mainsteam.stm.ct.bo.MsCtMetrics;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public interface CtMetricsMapper {

    public void getCtMetrics(Page<MsCtMetrics,MsCtMetrics> page);

    MsCtMetrics getByName(@Param("name") String name);

	public MsCtMetrics selectById(@Param("id")Integer id);
}
