package com.mainsteam.stm.profilelib.dao;

import java.util.List;

import com.mainsteam.stm.profilelib.po.ProfileChangeHistoryPO;

/**
 * 
 * @author xiaoruqiang
 */
public interface ProfileChangeHistoryDAO {
	
	/**
	 * 通过ProfileChangeHistoryId 获取ProfileChangeHistory信息
	 * @param ProfileChangeHistoryId 
	 * @return
	 */
	public List<ProfileChangeHistoryPO> getHistoryByPO(ProfileChangeHistoryPO profileChangeHistoryPO) throws Exception;
	
	/**
	 * 通过profileChangeIds 获取ProfileChangeHistory信息
	 * @param profileChangeIds 
	 * @return
	 */
	public List<ProfileChangeHistoryPO> getHistoryByProfileChangeIds(List<Long> profileChangeIds) throws Exception;
	
	
	/**
	 * 获取所有的信息
	 * @return
	 */
	public List<ProfileChangeHistoryPO> getAllFailedHistory() throws Exception;
	
	
	/**
	 * 新增策略改变历史信息
	 * @param ProfileChangeHistoryDAO
	 * @return
	 */
	public int insertProfileChangeHistory(ProfileChangeHistoryPO profileChangeHistoryPO) throws Exception;
	
	/**
	 * 新增策略改变历史信息
	 * @param ProfileChangeHistoryDAOs
	 * @return
	 */
	public int insertProfileChangeHistorys(List<ProfileChangeHistoryPO> profileChangeHistoryPOs) throws Exception;
	
	/**
	 *  更新操作
	 */
	public int updateProfileChangeHistory(ProfileChangeHistoryPO profileChangeHistoryPO) throws Exception;
	
	/**
	 * 修改策略改变历史信息
	 * @param ProfileChangeHistoryDAOs
	 * @return
	 */
	public int updateProfileChangeHistorys(List<ProfileChangeHistoryPO> profileChangeHistoryPOs) throws Exception;
}
