package com.mainsteam.stm.topo.web.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.topo.api.FTMSMapService;

/**
 * 此action专为FTMS项目服务
 */
@Controller
@RequestMapping("topo/ftms")
public class FTMSMapAction {
	@Autowired
	private FTMSMapService service;
	@ResponseBody
	@RequestMapping("flowlist")
	public JSONObject flowList(Long mapid,Integer level){
		JSONObject retn = new JSONObject();
		try {
			retn.put("info",service.flowList(mapid, level));
			retn.put("status",200);
		} catch (Exception e) {
			retn.put("status",700);
			retn.put("msg",e.getMessage());
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping("tip")
	public JSONObject tip(Long id,Integer level){
		JSONObject retn = new JSONObject();
		try {
			retn.put("info",service.tip(id,level));
			retn.put("status",200);
		} catch (Exception e) {
			retn.put("status",700);
			retn.put("msg",e.getMessage());
		}
		return retn;
	}
}
