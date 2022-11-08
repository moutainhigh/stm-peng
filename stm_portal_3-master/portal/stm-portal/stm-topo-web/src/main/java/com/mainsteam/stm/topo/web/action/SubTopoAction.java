package com.mainsteam.stm.topo.web.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.topo.api.ITopoAuthSettingApi;
import com.mainsteam.stm.topo.api.SubTopoService;
import com.mainsteam.stm.topo.bo.SubTopoBo;
import com.mainsteam.stm.topo.bo.TopoAuthSettingBo;
import com.mainsteam.stm.topo.enums.TopoType;
@Controller
@RequestMapping(value="topo/subtopo")
public class SubTopoAction extends BaseAction{
	private final Logger logger = LoggerFactory.getLogger(SubTopoAction.class);
	//权限控制
	@Autowired
	private ITopoAuthSettingApi authSvc;
	@Autowired
	private SubTopoService subtopoService;
	
	/**
	 * 拓扑图右侧二层拓扑排序
	 * @param subtopoTree
	 * @return
	 */
	@RequestMapping("/nav/sort")
	public JSONObject updateNavSort(String subtopoTree){
		try{
			subtopoService.updateNavSort(subtopoTree);
			return super.toSuccess("排序成功");
		}catch(Exception e){
			logger.error("拓扑树排序异常",e);
			return super.toJsonObject(700, "排序异常");
		}
	}
	
	@ResponseBody
	@RequestMapping(value="createOrUpdateSubtopo",method=RequestMethod.POST)
	public JSONObject createOrUpdateSubtopo(SubTopoBo sb,Long[] downLoadMoveIds,Long[] delIds,Long[] uploadMoveIds){
		JSONObject retn = new JSONObject();
		Long topoId=sb.getId();
		if(topoId==null){
			topoId=sb.getParentId();
		}
		if(authSvc.hasAuth(topoId, new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			try {
				JSONObject info = subtopoService.createOrUpdateSubtopo(sb,downLoadMoveIds,uploadMoveIds,delIds);
				retn.put("status", 200);
				retn.put("msg","更新成功");
				retn.put("data", info);
			} catch (Exception e) {
				retn.put("status", 700);
				retn.put("msg",e.getMessage());
			}
		}else{
			retn.put("status", 700);
			retn.put("msg", "无权限操作");
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="isTopoRoomEnabled",method=RequestMethod.POST)
	public JSONObject isTopoRoomEnabled(){
		JSONObject retn = new JSONObject();
		retn.put("enabled",subtopoService.isTopoRoomEnabled());
		retn.put("status",200);
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="addNewElementToSubTopo",method=RequestMethod.POST)
	public JSONObject addNewElementToSubTopo(SubTopoBo sb){
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(sb.getParentId(), new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			retn.put("code", 200);
			subtopoService.addNewElementToSubTopo(sb);
		}else{
			retn.put("code", 700);
			retn.put("msg", "无权限操作");
		}
		return retn;
	}
	/**
	 * 更新拓扑的属性，包括背景图，缩放比例，移动位置等
	 * @param sb
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="updateAttr",method=RequestMethod.POST)
	public JSONObject updateAttr(SubTopoBo sb){
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(sb.getId(), new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			retn.put("state", 200);
			sb.setParentId(null);
			subtopoService.updateAttr(sb);
			retn.put("subtopo", JSON.toJSON(sb));
		}else{
			retn.put("state", 700);
			retn.put("msg", "无权限操作");
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="getAttr",method=RequestMethod.POST)
	public JSONObject getAttr(Long id){
		return subtopoService.getAttr(id);
	}
	@ResponseBody
	@RequestMapping(value="getSubTopoIdBySubTopoName",method=RequestMethod.POST)
	public JSONObject getSubTopoIdBySubTopoName(String name){
		return subtopoService.getSubTopoIdBySubTopoName(name);
	}
	@ResponseBody
	@RequestMapping(value="getTopoType",method=RequestMethod.POST)
	public JSONObject getTopoType(Long topoId){
		JSONObject retn = new JSONObject();
		TopoType type=subtopoService.getTopoType(topoId);
		retn.put("id", type.getId());
		retn.put("name",type.getName());
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="deleteRoom",method=RequestMethod.POST)
	public JSONObject deleteRoom(Long id){
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(id, new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			retn.put("state", 200);
			subtopoService.deleteRoom(id);
		}else{
			retn.put("state", 700);
			retn.put("msg", "无权限操作");
		}
		return retn;
	}
}
