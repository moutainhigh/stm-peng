package com.mainsteam.stm.topo.api;

import com.alibaba.fastjson.JSONObject;

public interface ResourceService {

	String getAlarmInfo(Long instanceId);

	JSONObject nodeTooltipInfo(Long instanceId);

}
