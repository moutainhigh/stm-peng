package com.mainsteam.stm.portal.business.dao;

import java.util.List;

import com.mainsteam.stm.portal.business.bo.BizMainBo;
import com.mainsteam.stm.portal.business.bo.BizUserRelBo;

public interface IBizUserRelDao {
	
	/**
	 * 获取单业务系统用户权限集合
	 * @param long id
	 * @return List<BizUserRelBo>
	 */
	public List<BizUserRelBo> getUserlistByBizId(long bizId,String name,long domainId);
	
	/**
	 * 插入无查看权限用户
	 * @param List<BizUserRelBo> list
	 * @return int
	 */
	public int insertSet(BizUserRelBo bo);
	
	/**
	 * 通过业务ID检查数据数量
	 * @param long biz_id
	 * @return int
	 */
	public int getCount(long biz_id, List<Long> user_ids);
	
	/**
	 * 根据用户id获取可查看业务系统集合
	 * @param long user_id
	 * @return List<BizMainBo>
	 */
	public List<BizMainBo> getBizlistByUserId(long user_id);
	
	/**
	 * 根据用户ID和业务ID查询是否有查看权限
	 * @param long user_id,long biz_id
	 * @return List<BizMainBo>
	 */
	public List<BizUserRelBo> checkUserView(long user_id,long biz_id);
	
	/**
	 * 根据业务ID删除查看权限
	 * @param List<Long> biz_id
	 * @return int
	 */
	public int deleteByBizId(List<Long> biz_ids, List<Long> user_ids);
}
