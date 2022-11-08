package com.mainsteam.stm.topo.web.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.topo.api.VlanService;

@Controller
@RequestMapping("topo/vlan")
public class VlanAction extends BaseAction{
	@Autowired
	private VlanService vlanService;
	/**
	 * 获取和Node相关的vlan信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="getVlanForNodeBo",method=RequestMethod.POST)
	public JSONObject getVlanForNodeBo(Long nodeId){
		Assert.notNull(nodeId);
		JSONObject retn = new JSONObject();
		JSONArray vlans = vlanService.getVlanForNodeBo(nodeId);
		retn.put("status", 200);
		retn.put("vlans", vlans);
		return retn;
	}
}
