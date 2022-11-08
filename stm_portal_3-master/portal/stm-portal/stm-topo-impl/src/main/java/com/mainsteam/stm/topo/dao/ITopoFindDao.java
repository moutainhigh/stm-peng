package com.mainsteam.stm.topo.dao;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.platform.dao.IBaseDao;
import com.mainsteam.stm.topo.bo.NodeBo;

public interface ITopoFindDao extends IBaseDao<NodeBo>{
	/**
	 * 删除所有的链路
	 */
	void trunkLinkAll();
	/**
	 * 删除所有的其他节点
	 */
	void trunkOtherNodeAll();
	/**
	 * 删除所有的节点
	 */
	void trunkNodeAll();
	/**
	 * 删除所有的子拓扑
	 */
	void trunkSubTopoAll();
	/**
	 * 删除所有没有实例化的链路
	 */
	void trunkUnInstanceLink();
	/**
	 * 删除该子拓扑下的所有节点 包括其链路
	 * @param subTopoId 子拓扑id
	 */
	void deleteSubTopo(Long subTopoId);
	/**
	 * 查找ip和id关系表
	 * @return
	 */
	Map<Long, String> findIp();
	/**
	 * 删除一堆node,同时干掉它们的连接关系线
	 * @param prepareDelete
	 */
	void deleteNodes(List<Long> prepareDelete);
	void trunkGroupAll();
	void deleteAllLink();

}
