package com.mainsteam.stm.topo.web.action;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.topo.api.ITopoAuthSettingApi;
import com.mainsteam.stm.topo.api.MapNodeService;
import com.mainsteam.stm.topo.bo.MapNodeBo;
import com.mainsteam.stm.topo.bo.TopoAuthSettingBo;
import com.mainsteam.stm.topo.enums.TopoType;
import com.mainsteam.stm.topo.util.TopoHelper;

@Controller
@RequestMapping(value="topo/map/node")
public class MapNodeAction {
	private Logger logger = Logger.getLogger(MapNodeAction.class);
	@Autowired
	private MapNodeService mapNodeService;
	@Autowired
	private ITopoAuthSettingApi authSvc;
	@ResponseBody
	@RequestMapping(value="relateResourceInstance",method=RequestMethod.POST)
	public JSONObject relateResourceInstance(MapNodeBo node){
		TopoHelper.beginLog("MapNodeAction.relateResourceInstance");
		JSONObject retn = new JSONObject();
		try {
			if(authSvc.hasAuth(new Long(TopoType.MAP_TOPO.getId()), new String[]{TopoAuthSettingBo.EDIT})){
				MapNodeBo newnode = mapNodeService.relateResourceInstance(node,node.getLevel());
				retn.put("status", 200);
				retn.put("msg","关联成功");
				retn.put("node", JSON.toJSON(newnode));
			}else{
				throw new RuntimeException("没编辑权限");
			}
		} catch (Exception e) {
			retn.put("status", 700);
			retn.put("msg",e.getMessage());
			logger.error(e);
		}
		TopoHelper.endLog("MapNodeAction.relateResourceInstance");
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="cancelRelateResourceInstance",method=RequestMethod.POST)
	public JSONObject cancelRelateResourceInstance(String nodeid,Long mapid,Integer level){
		JSONObject retn = new JSONObject();
		try {
			if(authSvc.hasAuth(new Long(TopoType.MAP_TOPO.getId()), new String[]{TopoAuthSettingBo.EDIT})){
				mapNodeService.cancelRelateResourceInstance(nodeid,mapid,level);
				retn.put("status", 200);
				retn.put("msg","取消成功");
			}else{
				throw new RuntimeException("没编辑权限");
			}
		} catch (Exception e) {
			retn.put("status", 700);
			retn.put("msg",e.getMessage());
			logger.error(e.getMessage(),e);
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="updateAttr",method=RequestMethod.POST)
	public JSONObject updateAttr(String attr,Long id){
		JSONObject retn = new JSONObject();
		try {
			if(authSvc.hasAuth(new Long(TopoType.MAP_TOPO.getId()), new String[]{TopoAuthSettingBo.EDIT})){
				JSONObject result = mapNodeService.updateAttr(attr,id);
				retn.put("status", 200);
				retn.put("msg","更新成功");
				retn.put("result", result);
			}else{
				throw new RuntimeException("没编辑权限");
			}
		} catch (Exception e) {
			retn.put("status", 700);
			retn.put("msg",e.getMessage());
			logger.error(e);
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="updateNextMapIdAndLevel",method=RequestMethod.POST)
	public JSONObject updateNextMapIdAndLevel(String nodes){
		JSONObject retn = new JSONObject();
		try {
			if(authSvc.hasAuth(new Long(TopoType.MAP_TOPO.getId()), new String[]{TopoAuthSettingBo.EDIT})){
				mapNodeService.updateNextMapIdAndLevel(nodes);
				retn.put("status", 200);
				retn.put("msg","更新成功");
			}else{
				throw new RuntimeException("没编辑权限");
			}
		} catch (Exception e) {
			retn.put("status", 700);
			retn.put("msg",e.getMessage());
			logger.error(e);
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="getCountryList",method=RequestMethod.POST)
	public JSONObject getCountryList(Long key,Integer level){
		JSONObject retn = new JSONObject();
		try {
			if(authSvc.hasAuth(new Long(TopoType.MAP_TOPO.getId()), new String[]{TopoAuthSettingBo.SELECT})){
				JSONArray info = mapNodeService.getCountryList(key,level);
				retn.put("status", 200);
				retn.put("info", info);
				retn.put("msg","更新成功");
			}else{
				throw new RuntimeException("没查看权限");
			}
		} catch (Exception e) {
			retn.put("status", 700);
			retn.put("msg",e.getMessage());
			logger.error(e);
		}
		return retn;
	}
}
