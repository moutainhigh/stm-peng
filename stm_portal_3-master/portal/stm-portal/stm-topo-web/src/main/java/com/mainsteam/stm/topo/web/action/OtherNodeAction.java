package com.mainsteam.stm.topo.web.action;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.topo.api.ITopoAuthSettingApi;
import com.mainsteam.stm.topo.api.OtherNodeService;
import com.mainsteam.stm.topo.bo.OtherNodeBo;
import com.mainsteam.stm.topo.bo.TopoAuthSettingBo;

@Controller
@RequestMapping(value="topo/other")
public class OtherNodeAction extends BaseAction{
	Logger logger = Logger.getLogger(OtherNodeAction.class);
	//权限控制
	@Autowired
	private ITopoAuthSettingApi authSvc;
	@Autowired
	private OtherNodeService otherNodeService;
	@ResponseBody
	@RequestMapping(value="updateAttr")
	public JSONObject updateAttr(OtherNodeBo otherNode){
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(otherNode.getSubTopoId(), new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			retn = otherNodeService.updateAttr(otherNode);
		}else{
			retn.put("state", 700);
			retn.put("msg","无权限操作");
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="getById")
	public JSONObject getOtherNode(Long id){
		JSONObject nodeJson = otherNodeService.getById(id);
		return nodeJson;
	}
	@ResponseBody
	@RequestMapping(value="cabinetDeviceList")
	public JSONObject cabinetDeviceList(Long id){
		try {
			return otherNodeService.cabinetDeviceList(id);
		} catch (InstancelibException e) {
			logger.error("查询机房列表失败",e);
			return null;
		}
	}
	@ResponseBody
	@RequestMapping(value="addCabinet")
	public JSONObject addCabinet(OtherNodeBo ob){
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(ob.getSubTopoId(), new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			return otherNodeService.addCabinet(ob);
		}else{
			retn.put("status", 700);
			retn.put("msg", "无编辑权限");
			return retn;
		}
	}
	@ResponseBody
	@RequestMapping(value="updateCabinet")
	public JSONObject updateCabinet(OtherNodeBo ob){
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(ob.getSubTopoId(), new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			return otherNodeService.updateCabinet(ob);
		}else{
			retn.put("status", 700);
			retn.put("msg", "无编辑权限");
			return retn;
		}
	}
	@ResponseBody
	@RequestMapping(value="removeCabinet")
	public JSONObject removeCabinet(Long subTopoId,Long id){
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(subTopoId, new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			return otherNodeService.removeCabinet(id);
		}else{
			retn.put("status", 700);
			retn.put("msg", "无编辑权限");
			return retn;
		}
	}
	@ResponseBody
	@RequestMapping(value="getCabinetState")
	public JSONObject getCabinetState(Long[] ids){
		JSONObject retn = new JSONObject();
		try {
			JSONObject info = otherNodeService.getCabinetsState(ids);
			retn.put("info",info);
			retn.put("status",200);
		} catch (Exception e) {
			retn.put("status",700);
			retn.put("msg",e.getMessage());
		}
		return retn;
	}
}
