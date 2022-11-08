package com.mainsteam.stm.topo.api;

import com.alibaba.fastjson.JSONObject;

public interface GfMapService {

	JSONObject whole();

	JSONObject province(Long mapid);

	JSONObject city(Long mapid);

	JSONObject getFlowListForMap(Long mapid);

}
