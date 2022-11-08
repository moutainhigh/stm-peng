package com.mainsteam.stm.portal.business.api;

import com.mainsteam.stm.portal.business.bo.BizEditBo;

public interface BizEditApi {

	/**
	 * 获取自定义指标值
	 * @param edit
	 * @return
	 */
	public String getBizMetricEditValue(BizEditBo edit);
	
	/**
	 * 恢复业务指标值
	 * @param edit
	 * @return
	 */
	public boolean recoverBizMetricEditValue(BizEditBo edit);
	
	/**
	 * 添加或者修改自定义指标值
	 * @return
	 */
	public boolean insertOrUpdateMetricEditValue(BizEditBo edit);
	
}
