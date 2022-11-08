package com.mainsteam.stm.topo.dao;

import java.util.HashMap;
import java.util.List;

import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.OtherNodeBo;

public interface IOthersNodeDao {
	/**
	 * 通过id删除节点
	 * @param deleteIds
	 */
	void deleteByIds(List<Long> deleteIds);
	/**
	 * 更新一个链表的所有元素，都是由id的
	 * @param updates
	 */
	void updateList(List<OtherNodeBo> updates);
	/**
	 * 保存一个链表里所有元素
	 * @param saves
	 */
	void saveList(List<OtherNodeBo> saves);
	/**
	 * 保存
	 * @param otherNodeBo
	 * @return
	 */
	public int save(OtherNodeBo otherNodeBo);
	/**
	 * 通过子拓扑id获取其他节点列表
	 * @param id
	 * @return
	 */
	List<OtherNodeBo> getBySubTopoId(Long id);
	/**
	 * 获取主键ID
	 * @return id
	 */
	public ISequence getSeq();
	
	/**
	 * 获取所有数据
	 * @return
	 */
	public List<OtherNodeBo> getAll();
	/**
	 * 更新属性
	 * @param otherNode
	 */
	void updateAttr(OtherNodeBo otherNode);
	/**
	 * 通过id获取
	 * @param id
	 * @return
	 */
	OtherNodeBo getById(Long id);
	/**
	 * 判读机柜名称是否重复
	 * @param name 机柜名称
	 * @param subTopoId 所属机房
	 * @return
	 */
	int isCabinetRepeatName(String name, Long subTopoId);
	/**
	 * 
	 * @return
	 */
	List<OtherNodeBo> getAllCabinets();
	/**
	 * 
	 * @param nodeIds
	 * @return
	 */
	List<Long> getRoomByNodeIds(List<Long> nodeIds);
	
	List<OtherNodeBo> findCabinetInRoom(Long id);
	
	List<OtherNodeBo> getByIds(Long[] ids);
	
	void updateOtherZIndexById(HashMap map);
}
