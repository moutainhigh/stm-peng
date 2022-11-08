package com.mainsteam.stm.topo.dao;

import java.util.List;

import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.TopoAuthSettingBo;

public interface ITopoAuthSettingDao {
	
	public ISequence getSeq();
	
	/**
	 * 清空表
	 */
	public void truncateAll();
	
	/**
	 * 查询所有数据
	 * @return
	 */
	public List<TopoAuthSettingBo> getAll();

	/**
	 * 根据拓扑id查询权限设置信息
	 * @param topoId
	 * @return
	 */
	public List<TopoAuthSettingBo> getAuthSettingBosByTopoId(Long topoId);
	
	/**
	 * 根据拓扑id删除已配置的权限信息
	 * @param subtopoId
	 * @return
	 */
	public int deleteBySubtopoId(Long subtopoId);

	/**
	 * 保存子拓扑的用户权限设置信息
	 * @param settingBo
	 * @return
	 */
	public int save(TopoAuthSettingBo settingBo);
	/**
	 * 检查是否有相应的权限
	 * @param userId 用户id
	 * @param topoId 拓扑id
	 * @param modes
	 * @return
	 */
	public boolean hasAuth(Long userId, Long topoId, List<String> modes);
	
	/**
	 * 根据用户id和拓扑图id查询权限设置信息
	 * @param userId
	 * @param topoId
	 * @return
	 */
	public TopoAuthSettingBo getAuthSetting(Long userId, Long topoId);
	
	/**
	 * 通过用户id获取所有该用户能够查看到的拓扑图
	 * @param id 用户id
	 * @return 拓扑列表
	 */
	public List<Long> getAllReadOnlyTopo(Long id);
}
