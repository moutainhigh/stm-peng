package com.mainsteam.stm.topo.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.topo.bo.OtherNodeBo;

public interface OtherNodeService {

	/**
	 * 获取其他图元的告警状态
	 * @param subTopoId
	 * @param linkMetricId
	 * @param nodeMetricId
	 * @return
	 */
	JSONArray getOtherState(Long subTopoId,String linkMetricId,String nodeMetricId);
	
	JSONObject updateAttr(OtherNodeBo otherNode);

	JSONObject getById(Long id);

	JSONObject cabinetDeviceList(Long id) throws InstancelibException ;

	JSONObject addCabinet(OtherNodeBo ob);

	JSONObject updateCabinet(OtherNodeBo ob);

	JSONObject removeCabinet(Long id);

	JSONObject getCabinetsState(Long[] ids);

}
