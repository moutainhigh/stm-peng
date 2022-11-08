package com.mainsteam.stm.portal.business.api;

import java.util.List;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.portal.business.bo.BizCapMetricBo;
import com.mainsteam.stm.portal.business.bo.BizCapMetricDataBo;

public interface BizCapMetricApi {

	public List<BizCapMetricBo> getAllByBizIdAndMetric(long bizid, int type);

	public List<BizCapMetricBo> getAllByBizIdAndMetric(long bizid, String type);

	public List<Long> getByBizIdAndMetric(long bizid, String type);

	public List<Long> getInfoByBizIdAndMetric(long bizid, int type);

	public int insertInfo(BizCapMetricBo infoBo);

	public int deleteInfo(List<Long> id);

	public int deleteByInfo(long bizid, String metric);
/**
 * 封装树对象
 * @param instances
 * @return
 */
	public List<BizCapMetricDataBo> getData(List<ResourceInstance> instances);
}
