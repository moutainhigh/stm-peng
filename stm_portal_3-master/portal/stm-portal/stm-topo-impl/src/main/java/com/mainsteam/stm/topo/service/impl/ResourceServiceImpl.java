package com.mainsteam.stm.topo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.topo.api.ResourceService;
@Service
public class ResourceServiceImpl implements ResourceService{
	@Autowired
	private DataHelper dataHelper;
	@Override
	public String getAlarmInfo(Long instanceId) {
		return dataHelper.getLatestMsgAlarmEventForInstance(instanceId);
	}
	@Override
	public JSONObject nodeTooltipInfo(Long instanceId) {
		JSONObject info = new JSONObject();
		ResourceInstance res = dataHelper.getResourceInstance(instanceId);
		String[] vendorInfo = dataHelper.getResourceInstanceVendorInfo(res);
		info.put("vendor", vendorInfo[0]);
		info.put("series", vendorInfo[1]);
		info.put("ip", dataHelper.getResourceInstanceManageIp(res));
		info.put("name", dataHelper.getResourceInstanceShowName(res));
		return info;
	}
}
