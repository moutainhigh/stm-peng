package com.mainsteam.stm.topo.dao;

import java.util.List;

import com.mainsteam.stm.topo.bo.MapLineBo;

public interface FTMSDao {
	List<MapLineBo> getLinesInMap(Long mapid);

	MapLineBo getLineById(Long lineId);
}
