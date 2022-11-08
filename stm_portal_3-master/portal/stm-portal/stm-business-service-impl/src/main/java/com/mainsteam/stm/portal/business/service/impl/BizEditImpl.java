package com.mainsteam.stm.portal.business.service.impl;

import javax.annotation.Resource;

import com.mainsteam.stm.portal.business.api.BizEditApi;
import com.mainsteam.stm.portal.business.bo.BizEditBo;
import com.mainsteam.stm.portal.business.dao.IBizEditDao;

public class BizEditImpl implements BizEditApi {

	@Resource
	private IBizEditDao bizEditDao;
	
	/**
	 * 获取自定义指标值
	 * @param edit
	 * @return
	 */
	@Override
	public String getBizMetricEditValue(BizEditBo edit) {
		String metricValue = bizEditDao.getBizMeticValue(edit);
		return metricValue;
	}

	/**
	 * 恢复业务指标值
	 * @param edit
	 * @return
	 */
	@Override
	public boolean recoverBizMetricEditValue(BizEditBo edit) {
		if(bizEditDao.deleteBizMetricValue(edit) > 0){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 添加或者修改自定义指标值
	 * @return
	 */
	@Override
	public boolean insertOrUpdateMetricEditValue(BizEditBo edit) {
		
		int resultCount = 0;
		
		if(bizEditDao.getBizMeticValue(edit) != null){
			//修改
			resultCount = bizEditDao.updateBizMetricValue(edit);
		}else{
			//添加
			resultCount = bizEditDao.insertBizMetricValue(edit);
		}
		
		if(resultCount > 0){
			return true;
		}else{
			return false;
		}
		
	}

}
