package com.mainsteam.stm.topo.api;

import com.alibaba.fastjson.JSONArray;

public interface VlanService {

	JSONArray getVlanForNodeBo(Long nodeId);

}
