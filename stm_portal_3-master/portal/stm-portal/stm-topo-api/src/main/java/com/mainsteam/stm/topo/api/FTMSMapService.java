package com.mainsteam.stm.topo.api;

import com.alibaba.fastjson.JSONObject;

public interface FTMSMapService {
	JSONObject flowList(Long mapId,int level);

	JSONObject tip(Long id,int level);
}
