package com.mainsteam.stm.topo.api;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface TopoClipboardService {

	JSONArray copy(Long topoId, Long[] ids);

	JSONArray move(Long topoId, Long[] ids);

	void copyCopyLinks(List<Long> toCopyIds, Long topoId);

	void replace(JSONObject replaceMap,Long topoId,boolean isMove);

}
