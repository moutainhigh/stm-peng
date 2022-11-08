package com.mainsteam.stm.ipmanage.web.action;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.ipmanage.api.NetGroupService;
import com.mainsteam.stm.ipmanage.bo.NetGroup;
import com.mainsteam.stm.platform.web.action.BaseAction;
@Controller
@RequestMapping({ "/portal/ipmanage/netGroup" })
public class NetGroupAction extends BaseAction{
	Logger logger=Logger.getLogger(NetGroupAction.class);
	@Resource
	private NetGroupService netGroupService;
	
	@RequestMapping("/getNetGroupList")
	@ResponseBody
	public JSONObject getNetGroupList(){
		
		return toSuccess(netGroupService.getNetGroupList());
	}
	@RequestMapping("/insertNetGroup")
	@ResponseBody
	public JSONObject insertNetGroup(NetGroup netGroup){
		
		return toSuccess(netGroupService.insertNetGroup(netGroup));
	}
	@RequestMapping("/updateNetGroup")
	@ResponseBody
	public JSONObject updateNetGroup(NetGroup netGroup){
		
		return toSuccess(netGroupService.update(netGroup));
	}
	@RequestMapping("/deleteNetGroup")
	@ResponseBody
	public JSONObject delete(Integer id){
		
		return toSuccess(netGroupService.delete(id));
	}
}
