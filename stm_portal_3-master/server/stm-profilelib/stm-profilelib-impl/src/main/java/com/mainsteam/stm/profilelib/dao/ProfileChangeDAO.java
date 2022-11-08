package com.mainsteam.stm.profilelib.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.profilelib.po.ProfileChangePO;

/**
 * 
 * @author xiaoruqiang
 */
public interface ProfileChangeDAO {
	
	/**
	 * 
	 * @param page
	 * @return
	 */
	public List<ProfileChangePO> getProfileChange(Page<ProfileChangePO, ProfileChangePO> page);

	/**
	 * 更新策略改变信息
	 * @param ProfileChangePO
	 * @return
	 */
	public int updateProfileChange(ProfileChangePO profileChangePO) throws Exception;
	
	/**
	 * 更新策略改变信息
	 * @param ProfileChangePOs
	 * @return
	 */
	public int updateProfileChanges(List<ProfileChangePO> profileChangePOs) throws Exception;
	/**
	 * 更新策略改变信息
	 * @param ProfileChangePOs
	 * @return
	 *//*
	public int updateProfileChangeByPO(ProfileChangePO profileChangePO) throws Exception;
	*//**
	 * 更新策略改变信息
	 * @param ProfileChangePOs
	 * @return
	 *//*
	public int updateProfileChangeByPOs(List<ProfileChangePO> profileChangePOs) throws Exception;*/
	
	/**
	 * 新增策略改变信息
	 * @param ProfileChangePO
	 * @return
	 */
	public int insertProfileChange(ProfileChangePO profileChangePO) throws Exception;
	
	/**
	 * 新策略改变信息
	 * @param ProfileChangePOs
	 * @return
	 */
	public int insertProfileChanges(List<ProfileChangePO> profileChangePOs) throws Exception;
	
	/**
	 * 删除策略改变信息
	 * @param profileChangeId
	 * @return 
	 */
	public int deleteProfileChangeById(long profileChangeId) throws Exception;
	
}
