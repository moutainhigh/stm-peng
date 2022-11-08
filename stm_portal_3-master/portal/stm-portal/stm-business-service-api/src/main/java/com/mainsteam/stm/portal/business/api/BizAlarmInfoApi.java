package com.mainsteam.stm.portal.business.api;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.business.bo.BizAlarmInfoBo;
import com.mainsteam.stm.portal.business.bo.BizServiceBo;
import com.mainsteam.stm.portal.business.bo.BizWarnViewBo;

public interface BizAlarmInfoApi {
	/**
	 * 查询单条记录
	 * @param infoBo
	 * @return
	 */
	public BizAlarmInfoBo getAlarmInfo(BizAlarmInfoBo infoBo);	
	/**
	 * 添加
	 * @param infoBo
	 * @return
	 */
	public int insertBizAlarmInfo(BizAlarmInfoBo infoBo);

	/**
	 * 修改
	 * @param infoBo
	 */
	public int updateBizAlarmInfo(BizAlarmInfoBo infoBo);
	/**
	 * 通过业务id查告警设置信息
	 * @param bizid
	 * @return
	 */
	public BizAlarmInfoBo getAlarmInfoById(long bizid);
	/**
	 * 删除信息
	 * @param bizid 业务id
	 * @return
	 */
	public int deleteInfo(long bizid);
	/**
	 * 查告警信息
	 * @param page
	 * @param bizServiceBo
	 * @param status 资源告警状态
	 * @param nodeId 节点id 
	 * @throws Exception
	 */
	public void selectWarnViewPage(Page<BizWarnViewBo, BizWarnViewBo> page,
			BizServiceBo bizServiceBo,String status,String nodeId,String restore,Date starTime,Date endTime,String queryTime,String isCheckOne,String isCheckTwo) throws Exception;
}
