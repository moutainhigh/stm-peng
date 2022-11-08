package com.mainsteam.stm.topo.dao;

import java.util.List;

import com.mainsteam.stm.topo.bo.MapNodeBo;

public interface MapNodeDao {
	MapNodeBo findByNodeIdAndLevel(String nodeid,Integer level);

	void add(MapNodeBo node);

	void update(MapNodeBo dbnode);

	List<MapNodeBo> getNodesByMapId(Long id);

	void removeByNodeId(String nodeid);

	MapNodeBo findById(Long id);

	void updateNextMapIdAndLevel(Long id, Long nextMapId, Integer level);

	MapNodeBo getCountryByKey(Long key,Integer level);

	List<Long> instanceIdInLevel(int level);

	List<Long> instanceIdInCountry();
	
	MapNodeBo findAreaKeyByNodeIdAndMapId(MapNodeBo node);
}
