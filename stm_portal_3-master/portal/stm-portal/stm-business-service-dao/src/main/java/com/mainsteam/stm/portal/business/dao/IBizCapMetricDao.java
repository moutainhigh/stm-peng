package com.mainsteam.stm.portal.business.dao;

import java.util.List;

import com.mainsteam.stm.portal.business.bo.BizCapMetricBo;

public interface IBizCapMetricDao {
public List<BizCapMetricBo> getAllByBizIdAndMetric(long bizid,String name);

public List<Long> getInfoByBizIdAndMetric(long bizid,String name);

public int insertInfo(BizCapMetricBo infoBo);
public int deleteInfo(List<Long> id);
public int deleteByInfo(long bizid,String metric);
}
