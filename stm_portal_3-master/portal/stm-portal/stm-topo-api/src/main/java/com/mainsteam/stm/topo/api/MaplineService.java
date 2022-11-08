package com.mainsteam.stm.topo.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.topo.bo.MapLineBo;

public interface MaplineService {
	void addLine(MapLineBo line);
	void remove(Long id);
	Long convertToLink(JSONObject linkInfoJson);
	JSONArray unbindLinks(Long[] ids);

}
