package com.mainsteam.stm.profilelib;

import java.util.List;

import com.mainsteam.stm.profilelib.obj.ProfieChange;
import com.mainsteam.stm.profilelib.obj.ProfileChangeResult;

public interface ProfileChangeService {

	/**
	 * 批量修改ProfileChange状态值
	 * @param profieChanges
	 */
	public void updateProfileChangeSucceed(List<Long> profieChangeIds);

	/**
	 * 批量查询ProfileChange 值,使用升序
	 * @param pageSize 分页显示数
	 * @return
	 */
	public List<ProfieChange> getDispatcherProfileChanges(int pageSize);
	
	
	/**
	 * 批量添加profileChangeResults 值
	 * @param profileChangeResults
	 */
	public void addProfileChangeResults(List<ProfileChangeResult> profileChangeResults);
	
	/**
	 * 批量更新profileChangeResults 值
	 * @param profileChangeResults
	 */
	public void updateProfileChangeResultSucceed(List<ProfileChangeResult> profileChangeResults);
	
	/**
	 * 批量查询ProfileChangeResult 值
	 * @param ProfieChangeIds 
	 * @return
	 */
	public  List<ProfileChangeResult> getProfileChangeResultByProfileChangeId(List<Long> ProfieChangeIds);
	
}
