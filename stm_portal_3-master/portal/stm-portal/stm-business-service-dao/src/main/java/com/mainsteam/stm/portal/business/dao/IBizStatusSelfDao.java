package com.mainsteam.stm.portal.business.dao;


import java.util.List;

import com.mainsteam.stm.portal.business.bo.BizStatusSelfBo;

public interface IBizStatusSelfDao {
	/**
	 * 插入自定义数据
	 * @param bizStatusSelfBo
	 * @return
	 */
	int insert(BizStatusSelfBo bizStatusSelfBo);
	/**
	 * 根据业务应用id获取自定义数据集合
	 * @param bizSerId
	 * @return
	 */
	List<BizStatusSelfBo> getByBizSerId(long bizSerId);
	/**
	 * 根据业务应用id删除
	 * @param bizSerId
	 */
	void delByBizSerId (long bizSerId);
	/**
	 * 根据业务应用id和资源ids删除自自定义数据
	 * @param bizSerId
	 * @param instanceIds
	 */
	void delByBizSerIdAndInstanceIds (long bizSerId,Long[] instanceIds);
	/**
	 * 根据资源id删除
	 * @param bizSerId
	 */
	void delByInstanceId (long instanceId);
}
