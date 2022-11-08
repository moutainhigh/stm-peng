package com.mainsteam.stm.portal.business.dao;

import com.mainsteam.stm.portal.business.bo.BizAlarmInfoBo;

public interface IBizAlarmInfoDao {
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

public BizAlarmInfoBo getAlarmInfoById(long bizid);

/**
 * 删除告警设置信息
 * @param bizid
 * @return
 */
public int deleteInfo(long bizid);
}
