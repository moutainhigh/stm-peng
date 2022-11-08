package com.mainsteam.stm.topo.api;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface MapGraphService {
	JSONObject getMap(Long id);
	JSONObject getFlowListForMap(Long mapid);
	JSONObject getWholeCountryLevelInfo(int level);
	JSONObject getProvinceLevelInfo(Long mapid);
	JSONObject getCityLevelInfo(Long mapid);
	JSONArray refreshNodeState(Long[] nodeInstIds,String nodeMetricId);
	JSONArray  refreshLinkState(Long[] linkInstIds,String linkMetricId);
	JSONObject  getMapLine(Long id);
}
