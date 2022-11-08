package com.mainsteam.stm.portal.business.api;

import java.util.List;

import com.mainsteam.stm.portal.business.bo.BizMainBo;
import com.mainsteam.stm.portal.business.bo.BizUserRelBo;

public interface BizUserRelApi {
	
	/**
	 * 获取用户查看权限
	 * @param long bizId,String userName
	 * @return List<BizUserRelBo>
	 */
	public List<BizUserRelBo> getUserlistByBizId(long bizID,String name,long domainId);
	
	/**
	 * 更新权限
	 * @param long bizId,List<BizUserRelBo> list
	 * @return String
	 */
	public boolean update(long biz_id,List<BizUserRelBo> list, List<Long> user_ids);
	
	/**
	 * 获取用户查看权限
	 * @param long user_id
	 * @return List<BizMainBo>
	 */
	public List<BizMainBo> getBizlistByUserId(long user_id);
	
	/**
	 * 根据用户ID和业务ID查询是否有查看权限
	 * @param long user_id,long biz_id
	 * @return boolean
	 */
	public boolean checkUserView(long user_id, long biz_id);
	
	/**
	 * 根据业务ID删除查看权限数据
	 * @param List<Long> biz_ids, List<Long> user_ids
	 * @return boolean
	 */
	public String deleteBizById(List<Long> biz_ids, List<Long> user_ids);
}
