package com.mainsteam.stm.portal.business.dao;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.portal.business.bo.BizHealthHisBo;

public interface IBizHealthHisDao {

	/**
	 * 获取指定业务的当前状态
	 * @param id
	 * @return
	 */
	public int getBizStatus(long id);
	
	/**
	 * 获取指定业务的当前状态以及健康度
	 * @param id
	 * @return
	 */
	public BizHealthHisBo getBizHealthHis(long id);
	
	/**
	 * 添加一条业务历史告警数据
	 * @param healthHis
	 * @return
	 */
	public int insertHealthHis(BizHealthHisBo healthHis);
	
	/**
	 * 删除和业务相关的历史
	 * @return
	 */
	public int deleteHealthHisByBizId(long bizId);
	
	/**
	 * 根据时间范围获取业务的历史健康度信息
	 * @param map
	 * @return
	 */
	public List<BizHealthHisBo> getHealthByTimeScope(long bizId,Date startTime,Date endTime);
	
	/**
	 * 获取开始时间范围之前最近的一个采集点
	 * @param map
	 * @return
	 */
	public BizHealthHisBo getHealthFrontFirstScopeByStartTime(long bizId,Date startTime);
	
}
