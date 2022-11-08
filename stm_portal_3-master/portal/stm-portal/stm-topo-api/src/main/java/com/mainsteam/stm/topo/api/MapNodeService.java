package com.mainsteam.stm.topo.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.topo.bo.MapNodeBo;

public interface MapNodeService {
	MapNodeBo relateResourceInstance(MapNodeBo node,Integer level);
	void cancelRelateResourceInstance(String nodeid, Long mapid,Integer level);
	JSONObject updateAttr(String attr, Long id);
	void updateNextMapIdAndLevel(String nodes);
	JSONArray getCountryList(Long key,Integer level);

}
