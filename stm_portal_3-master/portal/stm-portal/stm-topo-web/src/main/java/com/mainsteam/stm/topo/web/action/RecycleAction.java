package com.mainsteam.stm.topo.web.action;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.topo.api.RecycleService;
@Controller
@RequestMapping(value="topo/recycle")
public class RecycleAction extends BaseAction {
	private Logger logger = Logger.getLogger(RecycleAction.class);
	@Autowired
	private RecycleService recycleService;
	@ResponseBody
	@RequestMapping(value="list")
	public JSONObject list(){
		JSONObject retn = new JSONObject();
		try {
			JSONArray items = recycleService.list();
			retn.put("items",items);
			retn.put("status",200);
		} catch (Exception e) {
			logger.error(e);
			retn.put("status",700);
			retn.put("msg",e.getMessage());
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="recover")
	public JSONObject recover(Long[] ids){
		JSONObject retn = new JSONObject();
		try {
			recycleService.recover(ids);
			retn.put("status",200);
		} catch (Exception e) {
			logger.error(e);
			retn.put("status",700);
			retn.put("msg",e.getMessage());
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="del")
	public JSONObject del(Long[] ids){
		JSONObject retn = new JSONObject();
		try {
			recycleService.del(ids);
			retn.put("status",200);
		} catch (Exception e) {
			logger.error(e);
			retn.put("status",700);
			retn.put("msg",e.getMessage());
		}
		return retn;
	}
}
