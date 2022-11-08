package com.mainsteam.stm.topo.api;

import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.topo.bo.SubTopoBo;
import com.mainsteam.stm.topo.enums.TopoType;

public interface SubTopoService {
	/**
	 * 更新拓扑右侧导航菜单排序号
	 * @param subtopoTree
	 */
	public void updateNavSort(String subtopoTree);
	
	/**
	 * 创建或者更新子拓扑
	 * @param SubTopoBo 需要更新的子拓扑
	 */
	public JSONObject createOrUpdateSubtopo(SubTopoBo sb,Long[] downMoveIds,Long[] upMoveIds,Long[] delIds);

	public void addNewElementToSubTopo(SubTopoBo sb);

	public void updateAttr(SubTopoBo sb);

	public JSONObject getAttr(Long subTopoId);

	public JSONObject getSubTopoIdBySubTopoName(String name);

	public TopoType getTopoType(Long topoId);

	public Set<Long> removeById(Long id,boolean recursive);

	public boolean isTopoRoomEnabled();

	public void deleteRoom(Long id);

	public Long getSubTopoId(String name);


}
