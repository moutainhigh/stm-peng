package com.mainsteam.stm.topo.dao;

import java.util.List;

import com.mainsteam.stm.topo.bo.MapLineBo;

public interface MapLineDao {

	/**
	 * 获取有instanceId的链路
	 * @return
	 */
	List<MapLineBo> getLinks();
	
	void add(MapLineBo line);

	List<MapLineBo> getLines(Long id);
	
	boolean isExsisted(MapLineBo line);

	void remove(Long id);

	MapLineBo getLineById(Long long1);
	
	void updateLink(MapLineBo line);
	
	/**
	 * 根据实例ids获取地图节点
	 * @param instanceIds
	 * @return
	 */
	public List<MapLineBo> findByInstanceIds(long[] instanceIds);

	void unbindLink(Long id);
}
