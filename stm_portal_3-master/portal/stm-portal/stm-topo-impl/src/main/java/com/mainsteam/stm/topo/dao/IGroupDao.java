package com.mainsteam.stm.topo.dao;

import java.util.List;

import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.GroupBo;


public interface IGroupDao {
	/**
	 * 保存分组
	 * @param groupBo
	 * @return
	 */
	public int save(GroupBo groupBo);
	
	/**
	 * 清空表
	 */
	public void truncateAll();

	void save(List<GroupBo> saveGroups);

	void update(List<GroupBo> updateGroups);

	List<GroupBo> getAll();

	void deleteByIds(List<Long> rgroupList);
	/**
	 * 通过id获取GroupBo对象
	 * @param id GroupBo的id
	 * @return GroupBo 对象
	 */
	GroupBo getById(Long id);
	/**
	 * 通过id列表查询GoupBo列表
	 * @param groupIds
	 * @return GroupBo列表
	 */
	Object getByIds(List<Long> groupIds);

	List<GroupBo> getBySubTopoId(Long id);
	/**
	 * 获取主键ID
	 * @return id
	 */
	public ISequence getSeq();
}
