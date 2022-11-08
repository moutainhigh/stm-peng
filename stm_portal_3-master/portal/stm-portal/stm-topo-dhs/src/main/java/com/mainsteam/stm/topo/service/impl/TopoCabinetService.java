package com.mainsteam.stm.topo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.topo.bo.OtherNodeBo;
import com.mainsteam.stm.topo.bo.SubTopoBo;
import com.mainsteam.stm.topo.dao.TopoDhsDao;

@Component
public class TopoCabinetService {
	@Autowired
	private TopoDhsDao dao;
	public JSONObject getCabinetInfo(Long id){
		List<OtherNodeBo> cabinets = dao.getCabinetByInstanceId(id);
		if(!cabinets.isEmpty()){
			JSONObject retn = new JSONObject();
			OtherNodeBo cab = cabinets.get(0);
			JSONObject attr = JSON.parseObject(cab.getAttr());
			SubTopoBo subtopo = dao.getSubTopoById(cab.getSubTopoId());
			retn.put("roomName", subtopo.getName());
			retn.put("cabinetName", attr.get("text"));
			return retn;
		}
		return null;
	}
}
