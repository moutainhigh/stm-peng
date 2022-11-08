package com.mainsteam.stm.portal.business.dao;

import com.mainsteam.stm.portal.business.bo.BizEditBo;

public interface IBizEditDao {

	/**
	 * 添加一条自定义指标值数据
	 * @param edit
	 * @return
	 */
	public int insertBizMetricValue(BizEditBo edit);
	
	/**
	 * 修改一条自定义指标值数据
	 * @param edit
	 * @return
	 */
	public int updateBizMetricValue(BizEditBo edit);
	
	/**
	 * 删除一条自定义指标值数据
	 * @param edit
	 * @return
	 */
	public int deleteBizMetricValue(BizEditBo edit);
	
	/**
	 * 获取自定义指标值数据
	 * @param edit
	 * @return
	 */
	public String getBizMeticValue(BizEditBo edit);
	
}
