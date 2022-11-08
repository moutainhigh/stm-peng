package com.mainsteam.stm.topo.dao;

import java.util.List;

import com.mainsteam.stm.platform.dao.IBaseDao;
import com.mainsteam.stm.topo.bo.HLJNode;

public interface HLJDao extends IBaseDao<HLJNode>{

	void addNode(HLJNode node);

	HLJNode getById(Long id);

	void updateInstanceIds(HLJNode dbNode);

	List<HLJNode> getNodesByMapId(String projection,Integer key);

	boolean existRelation(Integer parentMapId, Integer mapId);

	void delNullRelation(Integer parentMapId, Integer mapId);

	boolean existNullRelation(Integer parentMapId, Integer mapId);

	List<HLJNode> getNodesByNextMapId(Integer mapId);
	
}
